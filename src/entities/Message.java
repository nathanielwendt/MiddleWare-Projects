package entities;

import events.*;

public class Message {
	public String header;
	public Event event = null;
	public String debugString;

	public Message(){}
	
	//To-Do remove this constructor and associated data member after debugging is done
	public Message(String header){
		this.header = header;
	}
	
	public Message(String header, Event event){
		this.header = header;
		this.event =  event;
	}
	
	public boolean hasEvent(){
		return (this.event != null);
	}
	
	public String toString(){
		String returnString = "";
		returnString += "|" + this.header + "|";
		if(this.event != null){
			returnString += this.event.toString();
		}		
		return returnString;
	}
	
	public void populateMessage(String input){
		String [] segments = input.split("\\|");
		if(segments.length > 1)
			this.header = segments[1];
		if(segments.length >= 3){ //ensure that no seg faults will occur
			this.instantiateEventFromString(segments[2]);
			String temp = "|";
			for(int i = 3; i < segments.length; i++){
				temp += segments[i] + "|";
			}
			this.event.populateEvent(temp);
		}
	}
	
	public void instantiateEventFromString(String compare){
		compare = compare.toLowerCase();
		if(compare.equals("listItem")){}
		else if(compare.equals("bid"))
			this.event = new Bid();
		else if(compare.equals("bidupdate"))
			this.event = new BidUpdate();
		else if(compare.equals("salenotice"))
			this.event = new SaleNotice();
	}
}