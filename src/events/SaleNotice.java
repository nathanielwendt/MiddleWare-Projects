package events;

import entities.Event;
import includes.EventType;

public class SaleNotice extends Event {
	private int itemId;
	private int buyerId;
	private double saleValue;
	
	public SaleNotice(){
		this.eventType = EventType.saleNotice;
	}

	public SaleNotice(int itemId, int buyerId, double bidValue){
		this.eventType = EventType.saleNotice;
		this.itemId = itemId;
		this.buyerId = buyerId;
		this.saleValue = bidValue;
	}
	
	@Override
	protected String bodyToString() {
		String temp = "";
		temp += this.itemId + "|";
		temp += this.buyerId + "|";
		temp += this.saleValue + "|";
		return temp;
	}

	@Override
	protected void stringToBody(String string) {
		String[] segments = string.split("\\|");
		if(segments.length > 2){
			this.itemId = Integer.parseInt(segments[1]);
			this.buyerId = Integer.parseInt(segments[2]);
			this.saleValue = Double.parseDouble(segments[3]);
		}
	}
	
	public int getItemId(){
		return this.itemId;
	}
	
	public int getBuyerId(){
		return this.buyerId;
	}
	
	public double getSaleValue(){
		return this.saleValue;
	}
	
}