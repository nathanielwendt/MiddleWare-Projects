//@file Message.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ Message is an item sent between entities that contains a header (where it came from) and a body (an event to represent)

package entities;

import includes.EventType;
import setup.Init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Message {
	
	@Expose public static final int LEFT_BROKER_CHILD_LOCATION = 0;
	@Expose public static final int RIGHT_BROKER_CHILD_LOCATION = 1;
	@Expose public static final int FIRST_CLIENT_LOCATION = 2;
	@Expose public static final int SECOND_CLIENT_LOCATION = 3;
	@Expose public static final int PARENT_LOCATION = 4;
	
	@Expose private EventType eventType;
	@Expose private String header;
	@Expose private String uuid;
	@Expose private boolean[] messageSource;
	@Expose private String eventAsJson;
	private Event event;
	public String debugString;
	

	public Message(){}
	
	//To-Do remove this constructor and associated data member after debugging is done
	public Message(String header){
		this.header = header;
	}
	
	public Message(String header, Event event, String uuid){
		this.header = header;
		this.event =  event;
		this.setUuid(uuid);
		this.setEventAsJson(Init.gsonConverter.toJson(event));
		this.messageSource = new boolean[5];
		this.setEventType(event.eventType);
	}
	
	public boolean hasEvent(){
		return (this.event != null);
	}
	
	public String getHeader(){
		return this.header;
	}
	
	public Event getEvent(){
		return this.event;
	}
	
	public String toString(){
		String returnString = "";
		returnString += "|" + this.header + "|";
		if(this.event != null){
			returnString += this.event.toString();
		}		
		return returnString;
	}
	
	public boolean[] getMessageSourceArray(){
		return this.messageSource;
	}
	
	public String toJson(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();;
		return gson.toJson(this);
	}
	
	public static Message getObjectFromJson(String json){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();;
		return gson.fromJson(json, Message.class);
	}

	public String getEventAsJson() {
		return eventAsJson;
	}

	public void setEventAsJson(String eventAsJson) {
		this.eventAsJson = eventAsJson;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public void setLeftBrokerLocation(boolean value){
		this.messageSource[LEFT_BROKER_CHILD_LOCATION] = value;
	}
	
	public void setRightBrokerLocation(boolean value){
		this.messageSource[RIGHT_BROKER_CHILD_LOCATION] = value;
	}
	
	public void setFirstClientLocation(boolean value){
		this.messageSource[FIRST_CLIENT_LOCATION] = value;
	}
	
	public void setSecondClientLocation(boolean value){
		this.messageSource[SECOND_CLIENT_LOCATION] = value;
	}

	public void setParentLocation(boolean value){
		this.messageSource[PARENT_LOCATION] = value;
	}
	
	public void setMessageSourceValue(int location ,boolean value){
		this.messageSource[location] = value;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
}