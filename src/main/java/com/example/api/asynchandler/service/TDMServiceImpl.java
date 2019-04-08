
package com.example.api.asynchandler.service;



/*
 * Calling webClient.get() does not do anything with I/O. 
 * Subscribing to the Mono will start the whole thing. 
 * This reactive pipeline has no blocking I/O, so no thread will be blocked at any time. 
 * The remote might take a lot of time to answer (we can't solve latency for the whole world)
 */

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import com.example.api.asynchandler.configuration.AppConfig;

/*
 *  implementation refers to : https://www.baeldung.com/spring-5-webclient
 *  implementation refers to : https://springframework.guru/spring-5-webclient/
 */


@Service
@EnableScheduling
public class TDMServiceImpl implements TDMService {
	 
	  private AppConfig appConfig;
	  
	  private static final Logger logger = LoggerFactory.getLogger(TDMServiceImpl.class);
	  private WebClient TDM_Client;
	  
	  @Autowired
	  TDMServiceImpl (AppConfig appConfig){
		  this.appConfig = appConfig;
		  TDM_Client = WebClient.builder()
	                .baseUrl(appConfig.getEndpointTDM())
	                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
	                .build();
	  }
	  
	  
	// Webclient default timeout is 20second? need to handle timeout exception, fixedRate, fixedDelay
	// @Scheduled(fixedRate = 60000), no need to use Scheduled as this method will be triggered manually.
	@SuppressWarnings("rawtypes")
	public Mono<Map> getTDMstatus(String tdmUri){  
		
		  logger.info("getTdmStatus() called, " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
		  
		  return this.TDM_Client.get().uri(tdmUri)
				  .retrieve()
				  .bodyToMono(Map.class);
	  } 

}
