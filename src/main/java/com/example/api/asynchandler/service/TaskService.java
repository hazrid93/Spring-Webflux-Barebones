package com.example.api.asynchandler.service;

import java.util.Map;

import org.davidmoten.rx.jdbc.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.asynchandler.controller.TaskController;
import com.example.api.asynchandler.domain.Async_Task;
import com.example.api.asynchandler.parser.CreateResponse;
import com.example.api.asynchandler.parser.POST_MechanismDriver;
import com.example.api.asynchandler.repository.TaskRepository;

import io.reactivex.Flowable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Should have interface class for this class as well, will need to replace the Autowired class in TaskController
@Service
public class TaskService {

	  @Autowired
	  private TaskRepository repository;
	  
	  private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

	  public Flux<Async_Task> getAllTasks() {
		logger.info("getAllTasks service method called, " + ", " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
	    Flux <Async_Task> tasks = repository.getAllTasks()
	    								.flatMap(tx -> {
	    									return Flux.just(tx.value());
	    								});
	    return tasks;
	  }
	  
	  public Mono<CreateResponse> createNewTask(POST_MechanismDriver mechanismDriverJson) {
		 return repository.createNewTask(mechanismDriverJson);
	  }
	  
	  /*
	  public Mono<Async_Task> createNewTask(POST_MechanismDriver mechanismDriverJson) throws Exception {
		 logger.info( mechanismDriverJson.getIlAttributes().toString());
		 ObjectMapper objectMapper = new ObjectMapper();
		 objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		 String jsonString = objectMapper.writeValueAsString(mechanismDriverJson);
		 logger.info(jsonString);
		 IlAttributes result = objectMapper.readValue(jsonString, POST_MechanismDriver.class).getIlAttributes();
		 logger.info(result.toString());

		return null;
	  }
	*/

}