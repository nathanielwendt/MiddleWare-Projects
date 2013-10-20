package entities;

import setup.Init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Message {
	@Expose private String header;
	private Event event;
	@Expose private String eventAsJson;
	public String debugString;
	

	public Message(){}
	
	//To-Do remove this constructor and associated data member after debugging is done
	public Message(String header){
		this.header = header;
	}
	
	public Message(String header, Event event){
		this.header = header;
		this.event =  event;
		this.setEventAsJson(Init.gsonConverter.toJson(event));
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
	
}