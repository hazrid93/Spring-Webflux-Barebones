package com.example.api.asynchandler.repository;

import com.example.api.asynchandler.configuration.AppConfig;
import com.example.api.asynchandler.domain.Async_Task;
import com.example.api.asynchandler.parser.CreateResponse;
import com.example.api.asynchandler.parser.IlAttributes;
import com.example.api.asynchandler.parser.POST_MechanismDriver;
import com.example.api.asynchandler.parser.TdmAttributes;
import com.example.api.asynchandler.service.TDMService;

import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.Tx;
import org.davidmoten.rx.jdbc.pool.NonBlockingConnectionPool;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

// NOTE: IMPORTANT!, isolation for mariadb must be READ COMMITTED, the default level will cause problem in the database since default level will prevent INSERT to be updated
// into the database until other transaction that is still uncommitted is committed. https://dev.mysql.com/doc/refman/5.5/en/set-transaction.html

@Repository
public class TaskRepository {
	
	/*
  RxJavaPlugins plugins = new RxJavaPlugins() {
	   setErrorHandler(e -> { });
  }; */
	
  private Database db;
  private NonBlockingConnectionPool pool;
  
  private static final Logger logger = LoggerFactory.getLogger(TaskRepository.class);
  
  @Autowired
  private AppConfig appConfig;

  /*
  *
  * Using PostConstruct to wait AppConfig injection to finish to load application.properties values,
  * https://stackoverflow.com/questions/44681142/postconstruct-annotation-and-spring-lifecycle
  * if this Class is Autowired to another class it will wait until the PostConstruct method to be
  * runned first before the injection is executed.
  * 
  * Another approach is to use Autowired on the class constructor so that the dependencies are injected at construction time.
  *
  */
  @PostConstruct
  public void init() throws SQLException {
  /* 
   * 
   * Connection class removed because of related issue https://github.com/davidmoten/rxjava2-jdbc/issues/34
   *  pool needs to be able to create new connections whenever needed but you provided a singleton connection
   *  "jdbc:h2:file:./build/mydatabase"
   *  
   */
	  
   // Connection connection = DriverManager.getConnection(appConfig.getRxjava2Url(), appConfig.getRxjava2User(), appConfig.getRxjava2Password());
    this.pool =
        Pools.nonBlocking()
            .maxPoolSize(appConfig.getRxjava2MaxPool())
            .url(appConfig.getRxjava2Url())
            .user(appConfig.getRxjava2User())
            .password(appConfig.getRxjava2Password())
            .healthCheck(appConfig.getRxjava2Health())
            .idleTimeBeforeHealthCheck(appConfig.getRxjava2IdleTime(), TimeUnit.SECONDS)
            .connectionRetryInterval(appConfig.getRxjava2RetryInterval(), TimeUnit.SECONDS)
            .maxIdleTime(appConfig.getRxjava2MaxIdleTime(), TimeUnit.MINUTES)
            .build(); 
            
	
    this.db = Database.from(pool); 

  }
  
  
  /////////////////////////////
  //						 //
  //  Exposed API Section    //
  //						 //
  /////////////////////////////
  
  @SuppressWarnings("unchecked")
public Flux<Tx<Async_Task>> getAllTasks() {
	logger.info("getAllTasks repository method called, " + ", " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
    String sql = "SELECT id,il_attrib,tdm_attrib,task_start_date,task_compl_date,task_ack_date,metadata,tdm_status FROM async_task";
    

    /*
     * rs is ResultSet, will be use to retrieved the column value that we received from db and map into domain object Async_Task
     * this will repeat for each line of row we retrieved
     */
    
    /*
    Flowable<Async_Task> taskFlowable =
        db.select(sql)
        	//Automap
            .autoMap(Async_Task.class);*/
    
    // Had to roll back to concrete class approach, due to a issue where the data fetch from database has different result.
    Flowable<Tx<Async_Task>> taskFlowable =
            db.select(sql)
            	.transactedValuesOnly()
            	//Automap
                .get(
            		//ResultSet
                    rs -> {
                      Async_Task asyncTask = new Async_Task();
                      asyncTask.setId(rs.getInt("id"));
                      asyncTask.setIl_attrib(rs.getString("il_attrib"));
                      asyncTask.setTdm_attrib(rs.getString("tdm_attrib"));
                      asyncTask.setTask_start_date(rs.getObject("task_start_date", LocalDateTime.class));
                      asyncTask.setTask_compl_date(rs.getObject("task_compl_date", LocalDateTime.class));
                      asyncTask.setTask_ack_date(rs.getObject("task_ack_date", LocalDateTime.class));
                      asyncTask.setMetadata(rs.getString("metadata"));
                      asyncTask.setTdm_status(rs.getString("tdm_status"));

                      return asyncTask;
                    });
    //create Flux from another Flux 
    // what happen if db connection interrupted?
    return Flux.from(taskFlowable).doOnError(e -> {
		if( e instanceof SQLNonTransientConnectionException) {
			logger.info("SQLNonTransientConnectionException detected!");
			logger.info("Re-creating database connection");
			this.pool =
    		        Pools.nonBlocking()
    		            .maxPoolSize(appConfig.getRxjava2MaxPool())
    		            .url(appConfig.getRxjava2Url())
    		            .user(appConfig.getRxjava2User())
    		            .password(appConfig.getRxjava2Password())
    		            .healthCheck(appConfig.getRxjava2Health())
    		            .idleTimeBeforeHealthCheck(appConfig.getRxjava2IdleTime(), TimeUnit.SECONDS)
    		            .connectionRetryInterval(appConfig.getRxjava2RetryInterval(), TimeUnit.SECONDS)
    		            .maxIdleTime(appConfig.getRxjava2MaxIdleTime(), TimeUnit.MINUTES)
    		            .build();
    			
		    this.db = Database.from(pool); 
		}
	})
	.log();
  }
  
  
  public Mono<CreateResponse> createNewTask(POST_MechanismDriver postAttributes) {
	    logger.info("createNewTask repository method called, " + ", " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
	    String createSql = "INSERT INTO async_task (il_attrib, tdm_attrib, task_start_date, task_compl_date, task_ack_date, metadata, tdm_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    /*
	    Flowable<Tx<Integer>> taskFlowable =
	              db.update(createSql)
	                  .parameters(newMechanism.getIlAttributes_JSON(), newMechanism.getTdmAttributes_JSON(), newMechanism.getTaskStartDate(), "1000-01-01 00:00:00", "1000-01-01 00:00:00", "NA", "NA")
	                  .transacted()
	                  .counts();
		    
	        return taskFlowable;
	        */

	    return Mono.from(db.update(createSql)
			                  .parameters(postAttributes.getIlAttributes_JSON(), postAttributes.getTdmAttributes_JSON(), postAttributes.getTaskStartDate(), "1000-01-01 00:00:00", "1000-01-01 00:00:00", "NA", "NA")
	                          .returnGeneratedKeys()
	                          .get(
	                          		//ResultSet
	                                  rs -> {
	                                	  CreateResponse response = new CreateResponse();

	                                	  if(rs.getLong("value") != 0) {
	                                		  response.setStatus("SUCCESS");
	                                	  } else {
	                                		  response.setStatus("FAILED");
	                                	  }
	                                	  
	                                	  response.setGeneratedKey(rs.getLong("value"));
	                                	 
	                                	  return response;
                                  })        
	    )
		.doOnError(e -> {
    		if( e instanceof SQLNonTransientConnectionException) {
    			logger.info("SQLNonTransientConnectionException detected!");
    			logger.info("Re-creating database connection");
    			this.pool =
        		        Pools.nonBlocking()
        		            .maxPoolSize(appConfig.getRxjava2MaxPool())
        		            .url(appConfig.getRxjava2Url())
        		            .user(appConfig.getRxjava2User())
        		            .password(appConfig.getRxjava2Password())
        		            .healthCheck(appConfig.getRxjava2Health())
        		            .idleTimeBeforeHealthCheck(appConfig.getRxjava2IdleTime(), TimeUnit.SECONDS)
        		            .connectionRetryInterval(appConfig.getRxjava2RetryInterval(), TimeUnit.SECONDS)
        		            .maxIdleTime(appConfig.getRxjava2MaxIdleTime(), TimeUnit.MINUTES)
        		            .build();
        			
    		    this.db = Database.from(pool); 
    		}
    	});
	  }
  
  
  /////////////////////////////////
  //						     //
  //  Main Flow Service Section  //
  //						     //
  /////////////////////////////////
  
  
  // 50 can be adjustable, will do later.
  @SuppressWarnings("unchecked")
public Flux<Async_Task> get50Records() {
  /*
   * https://www.baeldung.com/rxjava-hooks
   * https://stackoverflow.com/questions/43525052/rxjava2-observable-take-throws-undeliverableexception
   * https://github.com/Polidea/RxAndroidBle/issues/383
   * https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
   * 
   * global error handler for any exceptions that can’t be delivered to a subscriber. 
   * By default, errors sent to the global error handler will be thrown, and application will crash.
   * https://stackoverflow.com/questions/44701664/in-rxjava-what-is-difference-between-rxjavaplugins-seterrorhandler-and-subscrib
   */
  RxJavaPlugins.setErrorHandler(error -> {
        //Log error or just ignore it
	    logger.info("error detected : " + error.getClass().getName());
		return;
    });
  
	logger.info("get50Records repository method called, " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
	
    String sql = "SELECT id,il_attrib,tdm_attrib,task_start_date,task_compl_date,task_ack_date,metadata,tdm_status " + 
					"FROM async_task " +
					"WHERE task_compl_date='1000-01-01 00:00:00' " +
					"ORDER BY async_task.id ASC, async_task.task_start_date ASC " +
					"LIMIT 50";
    
    
    	// Had to roll back to concrete class approach, due to a issue where the data fetch from database has different result.
    	Flowable<Async_Task> taskFlowable =  db.select(sql)
							            	//Automap
							                .get(
							            		//ResultSet
							                    rs -> {
							                      Async_Task asyncTask = new Async_Task();
							                      asyncTask.setId(rs.getInt("id"));
							                      asyncTask.setIl_attrib(rs.getString("il_attrib"));
							                      asyncTask.setTdm_attrib(rs.getString("tdm_attrib"));
							                      asyncTask.setTask_start_date(rs.getObject("task_start_date", LocalDateTime.class));
							                      asyncTask.setTask_compl_date(rs.getObject("task_compl_date", LocalDateTime.class));
							                      asyncTask.setTask_ack_date(rs.getObject("task_ack_date", LocalDateTime.class));
							                      asyncTask.setMetadata(rs.getString("metadata"));
							                      asyncTask.setTdm_status(rs.getString("tdm_status"));
							                      return asyncTask;
							                    });
    	
    	return Flux.from(taskFlowable).doOnError(e -> {
    		if( e instanceof SQLNonTransientConnectionException) {
    			logger.info("SQLNonTransientConnectionException detected!");
    			logger.info("Re-creating database connection");
    			this.pool =
        		        Pools.nonBlocking()
        		            .maxPoolSize(appConfig.getRxjava2MaxPool())
        		            .url(appConfig.getRxjava2Url())
        		            .user(appConfig.getRxjava2User())
        		            .password(appConfig.getRxjava2Password())
        		            .healthCheck(appConfig.getRxjava2Health())
        		            .idleTimeBeforeHealthCheck(appConfig.getRxjava2IdleTime(), TimeUnit.SECONDS)
        		            .connectionRetryInterval(appConfig.getRxjava2RetryInterval(), TimeUnit.SECONDS)
        		            .maxIdleTime(appConfig.getRxjava2MaxIdleTime(), TimeUnit.MINUTES)
        		            .build();
        			
    		    this.db = Database.from(pool); 
    		}
    	});
    	//.log();

  }
  
	@SuppressWarnings("unchecked")
public Flowable<Tx<Integer>> getForUpdate(String sql) {

	  RxJavaPlugins.setErrorHandler(error -> {
	        //Log error or just ignore it
		    logger.info("error detected : " + error.getClass().getName());
			return;
	    });
	  
		logger.info("getForUpdate repository method called, " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
		
	   /* 
	    String sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED; SELECT id,il_attrib,tdm_attrib,task_start_date,task_compl_date,task_ack_date,metadata,tdm_status " + 
						"FROM async_task " +
						"WHERE task_compl_date='1000-01-01 00:00:00' " +
						"FOR UPDATE";
		*/
	    
	    // Had to roll back to concrete class approach, due to a issue where the data fetch from database has different result.

	    	Flowable<Tx<Integer>> taskFlowable =  db.update(sql)
	    											.transacted().transactedValuesOnly().counts();

	    	return taskFlowable.doOnError(e -> {
	    		if( e instanceof SQLNonTransientConnectionException) {
	    			logger.info("SQLNonTransientConnectionException detected!");
	    			logger.info("Re-creating database connection");
	    			this.pool =
	        		        Pools.nonBlocking()
	        		            .maxPoolSize(appConfig.getRxjava2MaxPool())
	        		            .url(appConfig.getRxjava2Url())
	        		            .user(appConfig.getRxjava2User())
	        		            .password(appConfig.getRxjava2Password())
	        		            .healthCheck(appConfig.getRxjava2Health())
	        		            .idleTimeBeforeHealthCheck(appConfig.getRxjava2IdleTime(), TimeUnit.SECONDS)
	        		            .connectionRetryInterval(appConfig.getRxjava2RetryInterval(), TimeUnit.SECONDS)
	        		            .maxIdleTime(appConfig.getRxjava2MaxIdleTime(), TimeUnit.MINUTES)
	        		            .build();
	        			
	    		    this.db = Database.from(pool); 
	    		}
	    	});

	  }

}
