
package com.example.api.asynchandler.service;

import com.example.api.asynchandler.domain.Async_Task;

import reactor.core.publisher.Flux;

public interface MainFlowService {
	
	public void getRecord();
	public void start();
	
}

