
package com.example.api.asynchandler.parser;

/*
 * Query will be send towards /tech-domain-controllers/{tdc-id}}/services/{id},
 * The value will come from Mechanism Drivers using our POST endpoint.
 * Use this class to map to the response from TypeDriverManager
 * 
 */

// ~ NOT USED ~
// Incomplete or maybe not needed since Map is enough
public class GET_TypeDriverManager {

	 private int id;
	 private int tdc_id;
	 private int instance_id;
	 private String state;

	 public GET_TypeDriverManager() {
	
	 }
	
	 public GET_TypeDriverManager(int id, int tdc_id, int instance_id, String state) {
	     this.id = id;
	     this.tdc_id = tdc_id;
	     this.instance_id = instance_id;
	     this.state = state;
	 }

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getTdc_id() {
		return tdc_id;
	}
	
	public void setTdc_id(int tdc_id) {
		this.tdc_id = tdc_id;
	}
	
	public int getInstance_id() {
		return instance_id;
	}
	
	public void setInstance_id(int instance_id) {
		this.instance_id = instance_id;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
 
}
