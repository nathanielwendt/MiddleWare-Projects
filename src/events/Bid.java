package events;

import entities.Event;
import includes.EventType;

public class Bid extends Event {
	private int itemId;
	private double bidValue;
	
	public Bid(){
		this.eventType = EventType.bid;
	}

	public Bid(int itemId, double bidValue){
		this.eventType = EventType.bid;
		this.itemId = itemId;
		this.bidValue = bidValue;
	}
	
	@Override
	protected String bodyToString() {
		String temp = "";
		temp += itemId + "|";
		temp += bidValue + "|";
		return temp;
	}

	@Override
	protected void stringToBody(String string) {
		String[] segments = string.split("\\|");
		if(segments.length > 1){
			this.itemId = Integer.parseInt(segments[1]);
			this.bidValue = Double.parseDouble(segments[2]);
		}
	}
	
	public int getItemId(){
		return this.itemId;
	}
	
	public double getBidValue(){
		return this.bidValue;
	}
	
}
