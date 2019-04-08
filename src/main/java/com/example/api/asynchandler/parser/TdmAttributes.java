package com.example.api.asynchandler.parser;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id"
})
public class TdmAttributes {

	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("tdc-id")
	private Integer tdc_id;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	@JsonProperty("id")
	public Integer getId() {
		return id;
	}
	
	@JsonProperty("id")
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@JsonProperty("tdc-id")
	public Integer getTdc_id() {
		return tdc_id;
	}
	
	@JsonProperty("tdc-id")
	public void setTdc_id(Integer tdc_id) {
		this.tdc_id = tdc_id;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}
	
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	

}