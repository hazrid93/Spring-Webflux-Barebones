package com.example.api.asynchandler.parser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"status",
"keyId"
})
public class CreateResponse {
	@JsonProperty("keyId")
	private Long generatedKey;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("keyId")
	public void setGeneratedKey(Long generatedKey) {
		this.generatedKey = generatedKey;
	}
	
	@JsonProperty("keyId")
	public Long getGeneratedKey() {
		return generatedKey;
	}
	
	@JsonProperty("status")
	public String getStatus() {
		return status;
	}
	
	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}