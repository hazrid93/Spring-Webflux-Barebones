
package com.example.api.asynchandler.controller;

import com.example.api.asynchandler.parser.POST_MechanismDriver;
import com.example.api.asynchandler.service.TDMService;
import com.example.api.asynchandler.service.TaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class TaskController {

	private TaskService taskService;
	private TDMService tdmService;
	
	@Autowired
	TaskController (TDMService tdmService, TaskService taskService){
		this.tdmService = tdmService;
		this.taskService = taskService;
	}
	
	private final Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	@GetMapping(path = "/tasks", produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> getAllTasks() {
		//Create a mono containing the ResponseEntity
		logger.info("getAllTasks controller method called, " + ", " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
		return Mono.just(ResponseEntity.ok()
				//.header(arg0, arg1)
				.contentType(MediaType.APPLICATION_JSON)
				.body(taskService.getAllTasks())
			); 	    
    }
	
	@PostMapping(path = "/task", consumes=MediaType.APPLICATION_JSON_VALUE)
	  public Mono<ResponseEntity<?>> createNewTask(@RequestBody POST_MechanismDriver mechanismDriverJson) {
			//Create a mono containing the ResponseEntity
			logger.info("createNewTask method called, " + Thread.currentThread());
				return Mono.just(ResponseEntity.status(HttpStatus.CREATED)
						.contentType(MediaType.APPLICATION_JSON)
						.body(taskService.createNewTask(mechanismDriverJson)));
	}
	
}
