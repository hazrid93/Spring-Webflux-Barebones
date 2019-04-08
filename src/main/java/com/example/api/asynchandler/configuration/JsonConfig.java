
package com.example.api.asynchandler.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * This java class is to control the deserialization feature, more info at:
 * https://fasterxml.github.io/jackson-databind/javadoc/2.6/com/fasterxml/jackson/databind/DeserializationFeature.html
 * 
 * Implementation referred to:
 * https://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html#howto-customize-the-jackson-objectmapper
 */

@Configuration
public class JsonConfig {
	// why does autowiring this bean and using getObjectMapper doesn't work properly in POST_MechanismDriver.java?
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
	 MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
	 ObjectMapper objectMapper = new ObjectMapper();
	 objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 jsonConverter.setObjectMapper(objectMapper);
	 return jsonConverter;
	}
	
}
