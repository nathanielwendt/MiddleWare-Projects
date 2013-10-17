package entities;

import includes.EventType;
//All events will extend this class
public class Event implements java.io.Serializable{
	public EventType eventType;

	public Event(EventType evtp){
		this.eventType = evtp;
	}
	
	public Event StringToEvent(String stringEvent){
		Event returnEvent = new Event(EventType.bid);
		return returnEvent;
	}
	
	public String EventToString(Event event){
		return "s";
	}
}
