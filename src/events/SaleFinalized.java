package events;

import includes.EventType;
import setup.Init;

import com.google.gson.Gson;

import entities.Event;

public class SaleFinalized extends Event {
	private String itemUUID;
	private String buyerUUID;
	private double saleValue;
	
	public SaleFinalized(){
		this.eventType = EventType.saleNotice;
		this.isPublish = true;
	}

	public SaleFinalized(String buyerUUID,String itemUUID, double bidValue){
		this.eventType = EventType.saleNotice;
		this.itemUUID = itemUUID;
		this.buyerUUID = buyerUUID;
		this.saleValue = bidValue;
		this.isPublish = true;
	}
		
	public String getItemUUID(){
		return this.itemUUID;
	}
	
	public String getBuyerId(){
		return this.buyerUUID;
	}
	
	public double getSaleValue(){
		return this.saleValue;
	}
	
	public String toJson(){
		Gson gson = Init.gsonConverter;
		return gson.toJson(this);
	}
	
	public static SaleFinalized getObjectFromJson(String json){
		Gson gson = Init.gsonConverter;
		return gson.fromJson(json, SaleFinalized.class);
	}
	
}