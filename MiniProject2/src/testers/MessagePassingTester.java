package testers;

import includes.EventType;
import entities.Message;
import events.*;

//This class serves to show how message passing should be performed
public class MessagePassingTester {
	public static void main(String[] argv){
		
		
		//Event -> String
		Bid bid = new Bid(19204,22.24);
		//bid.isPublish = true;  default is set to false
		Message msg = new Message("header for bid",bid);
		System.out.println(msg.toString());
		
		SaleNotice bid2 = new SaleNotice(111,999,22.24);
		bid2.isPublish = true;
		Message msg4 = new Message("header for sale notice",bid2);
		System.out.println(msg4.toString());
		//
		
		//String -> Event
		String str = "|justaheadermessage|";
		Message msg2 = new Message();
		msg2.populateMessage(str);
		System.out.println(msg2.header);
		
		
		String str2 = "|blahblah|saleNotice|true|3503|999|66.40|";
		System.out.println(str2);
		Message msg3 = new Message();
		msg3.populateMessage(str2);
		System.out.println(msg3.header);
		
		if(msg3.hasEvent()){ //check this because otherwise msg2.event will be null and will throw an exception when looking for eventType
			if(msg3.event.eventType == EventType.bid){
				System.out.println(msg3.event.isPublish);
				System.out.println(((Bid) msg3.event).getItemId());
				System.out.println(((Bid) msg3.event).getBidValue());
			}
			if(msg3.event.eventType == EventType.bidUpdate){
				System.out.println(msg3.event.isPublish);
				System.out.println(((BidUpdate) msg3.event).getItemId());
				System.out.println(((BidUpdate) msg3.event).getBidUpdateValue());
			}
			if(msg3.event.eventType == EventType.saleNotice){
				System.out.println(msg3.event.isPublish);
				System.out.println(((SaleNotice) msg3.event).getItemId());
				System.out.println(((SaleNotice) msg3.event).getBuyerId());
				System.out.println(((SaleNotice) msg3.event).getSaleValue());
			}
		}
	}
}
