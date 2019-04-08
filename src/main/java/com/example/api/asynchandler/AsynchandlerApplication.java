package com.example.api.asynchandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.api.asynchandler.service.MainFlowService;
import com.example.api.asynchandler.service.TDMService;

import reactor.core.scheduler.Schedulers;

//Alternative to use 'implements CommandLineRunner' if need to setup stuff before SpringApplication is booted up.
@SpringBootApplication
public class AsynchandlerApplication {
	

	private TDMService tdmService;
	private MainFlowService mainService;
	
	@Autowired
	public AsynchandlerApplication(TDMService tdmService){
		this.tdmService = tdmService;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AsynchandlerApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(AsynchandlerApplication.class, args);
		
	}
	
	/*
	@Override
	public void run(String... args) throws Exception {
		this.mainService.start();
	} */
	
	

}

