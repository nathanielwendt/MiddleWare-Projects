package events;

import entities.Event;
import includes.EventType;

public class BidUpdate extends Event {
	private int itemId;
	private double bidUpdateValue;
	
	public BidUpdate(){
		this.eventType = EventType.bidUpdate;
	}

	public BidUpdate(int itemId, double bidValue){
		this.eventType = EventType.bidUpdate;
		this.itemId = itemId;
		this.bidUpdateValue = bidValue;
	}
	
	@Override
	protected String bodyToString() {
		String temp = "";
		temp += itemId + "|";
		temp += bidUpdateValue + "|";
		return temp;
	}

	@Override
	protected void stringToBody(String string) {
		String[] segments = string.split("\\|");
		if(segments.length > 1){
			this.itemId = Integer.parseInt(segments[1]);
			this.bidUpdateValue = Double.parseDouble(segments[2]);
		}
	}
	
	public int getItemId(){
		return this.itemId;
	}
	
	public double getBidUpdateValue(){
		return this.bidUpdateValue;
	}
	
	
}