package entities;

import includes.EventType;

//All events will extend this class
public abstract class Event{
	protected EventType eventType;
	protected boolean isPublish;

	public Event(){}
	
	public EventType getEventType(){
		return this.eventType;
	}
	
	public boolean isPublishType(){
		return this.isPublish;
	}
	
	public void setIsPublish(boolean value){
		this.isPublish = value;
	}
	
}
