package events;

import includes.EventType;
import setup.Init;

import com.google.gson.Gson;

import entities.Event;


public class InterestBidUpdate extends Event{

	private String itemUUID;
	
	public InterestBidUpdate(String itemUUID){
		this.itemUUID = itemUUID;
		this.eventType = EventType.interestbidupdate;
		this.isPublish = true;
	}
	
	public String toJson(){
		Gson gson = Init.gsonConverter;
		return gson.toJson(this);
	}
	
	public static InterestBidUpdate getObjectFromJson(String json){
		Gson gson = Init.gsonConverter;
		return gson.fromJson(json, InterestBidUpdate.class);
	}

	public String getItemUUID() {
		return itemUUID;
	}

	public void setItemUUID(String itemUUID) {
		this.itemUUID = itemUUID;
	}
}
