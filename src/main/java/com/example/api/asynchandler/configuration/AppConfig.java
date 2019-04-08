package com.example.api.asynchandler.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// Would need to configure default value for these properties or maybe use application.properties inside jar as default value?

@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class AppConfig {
	
	/*
	 * rxjava2 jdbc related configuration
	 * 
	 */
	 @Value("${rxjava2.jdbc.url}")
	 private String rxjava2Url;  
	 @Value("${rxjava2.jdbc.user}")
	 private String rxjava2User;
	 @Value("${rxjava2.jdbc.password}")
	 private String rxjava2Password;
	 @Value("${rxjava2.jdbc.healthCheck}")
	 private String rxjava2Health;
	 @Value("${rxjava2.jdbc.idleTimeBeforeHealthCheck}")
	 private Integer rxjava2IdleTime;
	 @Value("${rxjava2.jdbc.connectionRetryInterval}")
	 private Integer rxjava2RetryInterval;
	 @Value("${rxjava2.jdbc.maxPoolSize}")
	 private Integer rxjava2MaxPool;
	 @Value("${rxjava2.jdbc.maxIdleTime}")
	 private Integer rxjava2MaxIdleTime;
	 
	/*
	 * TypeDriverManager REST endpoint connection related configuration
	 */
	 @Value("${tdm.endpoint.baseurl}")
	 private String endpointTDM;
	 
	 /*
	 * TaskHandler REST endpoint connection related configuration
	 */
	 @Value("${taskhandler.endpoint.baseurl}")
	 private String endpointTaskHandler;
	 
	 
	 /*
	  *  Main Flow (Microservice 1) related configuration
	  */
	 @Value("${main.config.sleep}")
	 private Integer mainFlowTimerInterval;

	 
	 
	 /*
	 @Bean
	 public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	 }
	 */

	public String getEndpointTaskHandler() {
		return endpointTaskHandler;
	}

	public void setEndpointTaskHandler(String endpointTaskHandler) {
		this.endpointTaskHandler = endpointTaskHandler;
	}
	
	public Integer getMainFlowTimerInterval() {
		return mainFlowTimerInterval;
	}

	public void setMainFlowTimerInterval(Integer mainFlowTimerInterval) {
		this.mainFlowTimerInterval = mainFlowTimerInterval;
	}
	
	public String getEndpointTDM() {
		return endpointTDM;
	}

	public void setEndpointTDM(String endpointTDM) {
		this.endpointTDM = endpointTDM;
	}	 

	public Integer getRxjava2MaxIdleTime() {
		return rxjava2MaxIdleTime;
	}

	public void setRxjava2MaxIdleTime(Integer rxjava2MaxIdleTime) {
		this.rxjava2MaxIdleTime = rxjava2MaxIdleTime;
	}

	
	public String getRxjava2Url() {
		return rxjava2Url;
	}

	public void setRxjava2Url(String rxjava2Url) {
		this.rxjava2Url = rxjava2Url;
	}

	public String getRxjava2User() {
		return rxjava2User;
	}

	public void setRxjava2User(String rxjava2User) {
		this.rxjava2User = rxjava2User;
	}

	public String getRxjava2Password() {
		return rxjava2Password;
	}

	public void setRxjava2Password(String rxjava2Password) {
		this.rxjava2Password = rxjava2Password;
	}

	public String getRxjava2Health() {
		return rxjava2Health;
	}

	public void setRxjava2Health(String rxjava2Health) {
		this.rxjava2Health = rxjava2Health;
	}

	public Integer getRxjava2IdleTime() {
		return rxjava2IdleTime;
	}

	public void setRxjava2IdleTime(Integer rxjava2IdleTime) {
		this.rxjava2IdleTime = rxjava2IdleTime;
	}

	public Integer getRxjava2RetryInterval() {
		return rxjava2RetryInterval;
	}

	public void setRxjava2RetryInterval(Integer rxjava2RetryInterval) {
		this.rxjava2RetryInterval = rxjava2RetryInterval;
	}

	public Integer getRxjava2MaxPool() {
		return rxjava2MaxPool;
	}

	public void setRxjava2MaxPool(Integer rxjava2MaxPool) {
		this.rxjava2MaxPool = rxjava2MaxPool;
	}
	  
	  
}
