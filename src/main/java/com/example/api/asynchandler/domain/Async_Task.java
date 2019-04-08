
package com.example.api.asynchandler.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.api.asynchandler.controller.TaskController;
import com.example.api.asynchandler.parser.IlAttributes;
import com.example.api.asynchandler.parser.TdmAttributes;

// AUTOMAPPING(Interface mapping)
// https://github.com/davidmoten/rxjava-jdbc#automap-using-an-interface
// https://github.com/davidmoten/rxjava2-jdbc/blob/master/README.adoc#automap 
/*
public interface Async_Task {
	
	@Column("id")
	@Index(1)
    int getid();
	
	@Column("il_attrib")
	@Index(2)
    String getIlAttrib();
	
	@Column("tdm_attrib")
	@Index(3)
    String getTdmAttrib();
	
	@Column("task_start_date")
	@Index(4)
	Timestamp getTaskStartDate();
	
	@Column("task_compl_date")
	@Index(5)
	Timestamp getTaskComplDate();
	
	@Column("task_ack_date")
	@Index(6)
	Timestamp getTaskAckDate();
	
	@Column("metadata")
	@Index(7)
    String getMetadata();
	
	@Column("tdm_status")
	@Index(8)
    String getTdmStatus();
    
}
*/

public class Async_Task {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
	 
    private int id;
    private IlAttributes il_attrib;
    private TdmAttributes tdm_attrib;
    private Instant task_start_date;
    private Instant task_compl_date;
    private Instant task_ack_date;
    private String metadata;
    private String tdm_status;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public IlAttributes getIl_attrib() {
		return il_attrib;
	}

	public void setIl_attrib(String il_attrib) {
	 	ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    try {
	    	IlAttributes ilAttributes = objectMapper.readValue(il_attrib, IlAttributes.class);
	    	this.il_attrib = ilAttributes;
	    }
		 catch (Exception e) {
			logger.info(e.getMessage());
			this.il_attrib = null;
		}

	}

	public TdmAttributes getTdm_attrib() {
		return tdm_attrib;
	}

	public void setTdm_attrib(String tdm_attrib) {
	 	ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    try {
	    	TdmAttributes tdmAttributes = objectMapper.readValue(tdm_attrib, TdmAttributes.class);
	    	this.tdm_attrib = tdmAttributes;
	    }
		 catch (Exception e) {
			logger.info(e.getMessage());
			this.tdm_attrib = null;
		}
	}

	public Instant getTask_start_date() {
		return task_start_date;
	}

	public void setTask_start_date(LocalDateTime task_start_date) {
		//weird bug where the data fetch from the database is off the mark by a few hour?, necessary to do this way to avoid such issue.
		Instant startDateInstant = task_start_date.atOffset(ZoneOffset.UTC).toInstant();
		this.task_start_date = startDateInstant;
	}

	public Instant getTask_compl_date() {
		return task_compl_date;
	}

	public void setTask_compl_date(LocalDateTime task_compl_date) {
		Instant completeDateInstant = task_compl_date.atOffset(ZoneOffset.UTC).toInstant();
		this.task_compl_date = completeDateInstant;
	}

	public Instant getTask_ack_date() {
		return task_ack_date;
	}

	public void setTask_ack_date(LocalDateTime task_ack_date) {
		Instant acknowledgedDateInstant = task_ack_date.atOffset(ZoneOffset.UTC).toInstant();
		this.task_ack_date = acknowledgedDateInstant;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getTdm_status() {
		return tdm_status;
	}

	public void setTdm_status(String tdm_status) {
		this.tdm_status = tdm_status;
	}

    /*
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
    */
}
