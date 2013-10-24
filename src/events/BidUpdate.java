//@file BidUpdate.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ BidUpdate event is a notification that the previously held bid has been overwritten

package events;

import includes.EventType;
import setup.Init;

import com.google.gson.Gson;

import entities.Event;

public class BidUpdate extends Event {
	private String itemUUID;
	private double bidUpdateValue;
	private String bidderUUID;
	
	public BidUpdate(){
		this.eventType = EventType.bidUpdate;
		this.isPublish = true;
	}

	public BidUpdate(String bidderUUID,String itemUUID,double bidValue){
		this.eventType = EventType.bidUpdate;
		this.bidderUUID = bidderUUID;
		this.itemUUID = itemUUID;
		this.bidUpdateValue = bidValue;
		this.isPublish = true;
	}

	public String getItemUUID(){
		return this.itemUUID;
	}
	
	public double getBidUpdateValue(){
		return this.bidUpdateValue;
	}
	
	public String toJson(){
		Gson gson = Init.gsonConverter;
		return gson.toJson(this);
	}
	
	public static BidUpdate getObjectFromJson(String json){
		Gson gson = Init.gsonConverter;
		return gson.fromJson(json, BidUpdate.class);
	}

	public String getBidderUUID() {
		return bidderUUID;
	}

	public void setBidderUUID(String bidderUUID) {
		this.bidderUUID = bidderUUID;
	}
	
	
}