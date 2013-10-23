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
	private boolean autoMinBid = false; //added for the auto bidding

	public static void main(String[] args){
		SaleItem s = new SaleItem("Car","Mercedes",SaleItem.TIME_STAMP_IGNORE,1000,5000,"FSdf");
		Message b = new Message("message",s,s.getUuid());
		System.out.println(b.toJson());
		System.out.println(Message.getObjectFromJson(b.toJson()).getMessageSourceArray());
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

	//seller overrides bid value when it sees that it is a auto min bid and sets the
	//bid to be the minimum possible bid.  Safety checks to make sure the caller can do this
	public void setBidValue(double bidValue){
		if(this.autoMinBid){
			this.bidValue = bidValue;
		}
	}

	//added these for the auto bidding
	public void setAutoMinBid(){
		this.autoMinBid = true;
	}

	//added these for the auto bidding
	public boolean isAutoMinBid(){
		return this.autoMinBid;
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
