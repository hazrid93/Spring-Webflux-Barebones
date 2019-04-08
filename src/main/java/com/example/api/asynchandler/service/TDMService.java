
package com.example.api.asynchandler.service;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface TDMService {
	
	@SuppressWarnings("rawtypes")
	public Mono<Map> getTDMstatus(String tdmUri);
	
}

