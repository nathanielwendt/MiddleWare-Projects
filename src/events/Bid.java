package events;

import includes.EventType;
import setup.Init;

import com.google.gson.Gson;

import entities.Event;
import entities.Message;

public class Bid extends Event {
	private String itemUUID;
	private double bidValue;
	private String bidderUUID;
	
	public static void main(String[] args){
		Message b = new Message("message",new SaleItem("Car","Mercedes",SaleItem.TIME_STAMP_IGNORE,1000,5000));
		System.out.println(b.toJson());
		System.out.println(Message.getObjectFromJson(b.toJson()).getEventAsJson());
	}
	
	public Bid(){
		this.eventType = EventType.bid;
		this.isPublish = false;
	}

	public Bid(String bidderUUID,String itemUUID,double bidValue){
		this.eventType = EventType.bid;
		this.itemUUID = itemUUID;
		this.bidValue = bidValue;
		this.bidderUUID = bidderUUID;
		this.isPublish = false;
	}
	
	public String getItemUUID(){
		return this.itemUUID;
	}
	
	public double getBidValue(){
		return this.bidValue;
	}
	
	public String toJson(){
		Gson gson = Init.gsonConverter;
		return gson.toJson(this);
	}
	
	public static Bid getObjectFromJson(String json){
		Gson gson = Init.gsonConverter;
		return gson.fromJson(json, Bid.class);
	}

	public String getBidderUUID() {
		return bidderUUID;
	}

	public void setBidderUUID(String bidderUUID) {
		this.bidderUUID = bidderUUID;
	}

}
