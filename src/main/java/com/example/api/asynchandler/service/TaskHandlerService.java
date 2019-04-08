
package com.example.api.asynchandler.service;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface TaskHandlerService {
	
	@SuppressWarnings("rawtypes")
	public Mono<Map> patchTaskHandler(String taskHandlerUri);
	
}

