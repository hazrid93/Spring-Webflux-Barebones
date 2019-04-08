package com.example.api.asynchandler.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.api.asynchandler.controller.TaskController;

import java.sql.Timestamp;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"il_attributes",
"tdm_attributes",
"task_start_date"
})
public class POST_MechanismDriver {
	
	private static final Logger logger = LoggerFactory.getLogger(POST_MechanismDriver.class);
	
	@Autowired
	private MappingJackson2HttpMessageConverter myJackson;
	//NVI *change il_attributes to NVI
	@JsonProperty("il_attributes")
	private IlAttributes ilAttributes;
	@JsonProperty("tdm_attributes")
	private TdmAttributes tdmAttributes;
	@JsonProperty("task_start_date")
	private Timestamp taskStartDate;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("il_attributes")
	public IlAttributes getIlAttributes() {
		return ilAttributes;
	}

	@JsonProperty("il_attributes")
	public void setIlAttributes(IlAttributes ilAttributes) {
		this.ilAttributes = ilAttributes;
	}

	@JsonProperty("tdm_attributes")
	public TdmAttributes getTdmAttributes() {
		return tdmAttributes;
	}

	@JsonProperty("tdm_attributes")
	public void setTdmAttributes(TdmAttributes tdmAttributes) {
		this.tdmAttributes = tdmAttributes;
	}

	@JsonProperty("task_start_date")
	public Timestamp getTaskStartDate() {
		return taskStartDate;
	}
	
	// Store date into database as UTC format (timezone offsets are removed to become 0 offset).
	// If it is important to store actual user offset then would need another column to store zone information.
	// By using ZonedDateTime to store as LocalDateTime will get the date as UTC with zero zone information.
	// https://stackoverflow.com/questions/32437550/whats-the-difference-between-instant-and-localdatetime
	@JsonProperty("task_start_date")
	public void setTaskStartDate(LocalDateTime taskStartDate) {
		Timestamp startDateTS = Timestamp.valueOf(taskStartDate);
		this.taskStartDate = startDateTS;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	
	@JsonIgnore
	public String getIlAttributes_JSON() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    try {
			return objectMapper.writeValueAsString(getIlAttributes());
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			return "NA";
		}
	}
	
	@JsonIgnore
	public String getTdmAttributes_JSON() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    try {
			return objectMapper.writeValueAsString(getTdmAttributes());
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			return "NA";
		}
	}
	
	


}
