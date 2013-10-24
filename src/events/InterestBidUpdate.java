//@file InterestBidUpdate.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ InterestBidUpdate event that is generated when a buyer has in interest in an item

package events;

import includes.EventType;
import includes.UUIDGenerator;
import setup.Init;

import com.google.gson.Gson;

import entities.Event;


public class InterestBidUpdate extends Event{

	private String requestUUID;
	private String itemUUID;
	private String userUUID;
	
	public InterestBidUpdate(String userUUID,String itemUUID){
		this.itemUUID = itemUUID;
		this.setUserUUID(userUUID);
		this.requestUUID = UUIDGenerator.getNextUUID();
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

	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}

	public String getRequestUUID() {
		return requestUUID;
	}

	public void setRequestUUID(String requestUUID) {
		this.requestUUID = requestUUID;
	}
}
