package entities;

import includes.EventType;

//All events will extend this class
public abstract class Event{
	public EventType eventType;
	public boolean isPublish;

	public Event(){}
		
	//children classes override this class;
	public void populateEvent(String inputStr){
		String[] segments = inputStr.split("\\|");
		if(segments.length >= 2){
			this.isPublish = Boolean.parseBoolean(segments[1]);
			String temp = "|";
			for(int i = 2; i < segments.length; i++)
				temp += segments[i] + "|";
			this.stringToBody(temp);
		}
	}
	
	public String toString(){
		String string = "";
		string += this.eventType + "|";
		string += this.isPublish + "|";
		string += this.bodyToString();
		return string;
	}
	
	//children classes override this class
	protected abstract String bodyToString();
	
	//children classes override this class
	protected abstract void stringToBody(String string);
}
