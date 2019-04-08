
package com.example.api.asynchandler.service;

import java.sql.Timestamp;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.davidmoten.rx.jdbc.ResultSetMapper;
import org.davidmoten.rx.jdbc.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.example.api.asynchandler.configuration.AppConfig;
import com.example.api.asynchandler.domain.Async_Task;
import com.example.api.asynchandler.repository.TaskRepository;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// More information on scheduling at https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#elastic--

//@EnableScheduling
@Service
public class MainFlowServiceImpl implements MainFlowService {
	 
		private AppConfig appConfig;
		private TDMService tdmService;
		private TaskHandlerService taskHandlerService;
		
		@Autowired
		private TaskRepository repository;
		  
		private static final Logger logger = LoggerFactory.getLogger(MainFlowServiceImpl.class);
		  
		@Autowired
		MainFlowServiceImpl (AppConfig appConfig, TDMService tdmService, TaskHandlerService taskHandlerService){
			this.appConfig = appConfig;
			this.tdmService = tdmService;
			this.taskHandlerService = taskHandlerService;
		}
		
		// @PostConstruct
		// Start this service after springapplication is ready.
		@EventListener(ApplicationReadyEvent.class)
		@Override
		public void start(){
			 logger.info("Main flow execution: " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
			 try {
				 this.getRecord();
			 } catch (Exception e) {
				 logger.info("Error in main flow : " + e.getMessage());
			 }
		} 

		
		/////////////////////
		// Main Flow Logic //
		/////////////////////
		// NOTE: IMPORTANT!, isolation for mariadb must be READ COMMITTED, the default level will cause problem in the database since default level will prevent INSERT to be updated
		// into the database until other transaction that is still uncommitted is committed. https://dev.mysql.com/doc/refman/5.5/en/set-transaction.html
		
		@SuppressWarnings("unused")
		// Main flow execution is sequential, requires blocking approach but with Schedulers technique as blocking mitigation.
		@Override
		public void getRecord() {
			
			  // Get and process the record
			  repository.get50Records()
				  .publishOn(Schedulers.elastic()) 
				  .flatMap(data -> {
					  try {
						  /*
						   *  Query from TDM section
						   *  Generate SQL here for querying to TDM endpoint. Note: commit/rollback are handle automatically 
						   *  https://github.com/s/rxjava2-jdbc#transactions
						   */
						  
						//  String sql = "SET SESSION tx_isolation=\'READ COMMITTED\' ; SELECT * FROM async_task WHERE id=" + data.getId() + " FOR UPDATE NOWAIT";
						  String IsolationSql = "SET SESSION tx_isolation=\'READ-UNCOMMITTED\'";
						  String SelectForSql = "SELECT * FROM async_task WHERE id=" + data.getId() + " FOR UPDATE NOWAIT";
						  
						  repository.getForUpdate(IsolationSql)
						  		.doOnError( e -> logger.info("Error with getForUpdate in repository method : " + e.getMessage()))
						  		.flatMap(tx1 -> {
							  			 return Flowable.just(tx1.select(SelectForSql).transactedValuesOnly().getAs(Object.class)
							  					.doOnError( e -> { 
							  						logger.info("Error with getForUpdate lock row in MainFlowServiceImpl method : " + e.getMessage());
							  						throw new Exception(e);
							  					})
							  					.flatMap(tx2 -> {
							  						try {
											    		boolean LockStatus = false;
											    		
											    		if (data.getTdm_attrib() == null) {
											    			throw new Exception("The TdmAttributes object is empty");
											    		}
											    		 // Need to improve uri generation (use regex ?)
														  String tdmUri = appConfig.getEndpointTDM() + 
																  "/api/v1/tech-domain-controllers/" +
																  data.getTdm_attrib().getTdc_id() +
																  "/services/" +
																  data.getTdm_attrib().getId();
																  
														  logger.info("TDM URL : " + tdmUri);
		
														  Map<?,?> responseTdm = tdmService.getTDMstatus(tdmUri).block();
														  
														  if(responseTdm.get("state").equals("Active")) {
															  logger.info("Process status : Active");
															  
															  LocalDateTime taskComplDate = LocalDateTime.now();
															  Timestamp complDateTS = Timestamp.valueOf(taskComplDate);
															
															  return Flowable.just(tx2.update("UPDATE async_task SET tdm_status=\"SUCCESS\", task_compl_date=\"" + complDateTS + "\" WHERE id=:id;")
														    		 .parameter("id", data.getId()).transactedValuesOnly().counts()
														    		 .flatMap(tx3 -> {
														    			 // PATCH to task handler SECTION
														    			 // Need to parse actual content from db body for il_attributes to get the correct request and task id
														    			 // to get the correct reqId and taskId
														    			 try {
														    				 String taskHandlerUri = appConfig.getEndpointTaskHandler() + 
																					  "/requests/" +
																					  "13" +
																					  "/tasks/" +
																					  "123";
																					  
																			  logger.info("Task Handler URL : " + taskHandlerUri);
							
																			  Map<?,?> responseTaskHandler = taskHandlerService.patchTaskHandler(taskHandlerUri).block();
																			 
																			  LocalDateTime taskAckDate = LocalDateTime.now();
																			  Timestamp ackDateTS = Timestamp.valueOf(taskAckDate);
																			  
																			  // Exception in sendResponse? SECTION
																			  return Flowable.just(tx3.update("UPDATE async_task SET task_ack_date=\"" + ackDateTS + "\" WHERE id=:id;")
																			    		 .parameter("id", data.getId()).transactedValuesOnly().counts().blockingLast());
														    			 } catch (Exception e) {
														    				 logger.info("Fail to patch : " + e.getMessage());
														    				 return Flowable.empty();
														    			 }
														    		 })
														    		 .blockingLast());
														    	
														  } else {
															  logger.info("Process status : " + responseTdm.get("state").toString());
															  //handle if not active?
															  // TO-DO
															   return Flowable.just(tx2);
														  }	
							  					  } catch (Exception e) {
														  logger.info("Fail to update database: " + e.getMessage());
														  return Flowable.empty();
												  }
					  						}).blockingLast());
						  		}).blockingLast();
						  return Flux.just(data);
					  } catch (Exception e)  {
						  logger.info("Fail to get process response from TDM : " + e.getMessage());
						  return Flux.empty();
					  }
					
				  }) 
				  .doOnNext(data -> {
					  // Data logging, doOn... methods are side methods https://dzone.com/articles/rxjavas-side-effect-methods
					  // logger.info("Data value: " + String.valueOf(data.getId()));
				  })
				  .doOnError(e -> { 
					  	logger.info("Error with getRecord method in MainFlowService : " + e.getMessage());
				  })
				  //.onErrorResume(e -> repository.get50Records())
				  .delaySubscription(Duration.ofSeconds(appConfig.getMainFlowTimerInterval()))
				  .retry()
				  .repeat()
				  .blockLast();
		}		

}
