package buyer;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
import events.*;
 
public class Buyer {
	public String UUID;
	public LinkedBlockingQueue<Message> incoming = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> outgoing = new LinkedBlockingQueue<Message>();
	
    public static void main(String[] args) throws IOException {
    	Buyer buyer = new Buyer();
    	new BuyerIOThread(buyer.incoming, buyer.outgoing).start();
		buyer.SendNotice();
        while(true){
			try{
				Thread.sleep(3000);
				buyer.PrintNotices();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
        }
    }
    
    public void PrintNotices(){
    	Message nextMsg;
    	nextMsg = (Message) this.incoming.poll();
		if(nextMsg != null){
			System.out.println(nextMsg.debugString);		
		}
    	
    }
    
    public void SendNotice(){
    	Message nextMsg = new Message("hey I'm a buyer wanting to tell the brokers something");
    	this.outgoing.add(nextMsg);
    }
    
    
    //To-Do adapt SaleItem to fit this style, and reformat this method
    public void SubscribeSaleItem(String baseString,String modifierString,long timeStamp,double costLowerBound, double costUpperBound){
    	//SaleItem saleItem = new SaleItem(baseString,modifierString,timeStamp,costLowerBound,costUpperBound);
    	//saleItem.isPublish = false;
    	//Message saleNotice = new Message(this.UUID,saleItem);
    	//this.outgoing.add(saleNotice);
    }
    
    public void PublishBid(int itemId, double bidValue){
    	Bid bid = new Bid(itemId, bidValue);
    	bid.isPublish = true;
    	Message bidMsg = new Message(this.UUID,bid);
    	this.outgoing.add(bidMsg);
    }
    
    public void SubscribeBid(int itemId){
    	Bid bid = new Bid(itemId, 0.00);
    	bid.isPublish = false;
    	Message bidMsg = new Message(this.UUID,bid);
    	this.outgoing.add(bidMsg);
    }
    
    public void SubscribeBidUpdate(int itemId){
    	BidUpdate bidUpdate = new BidUpdate(itemId, 0.00);
    	bidUpdate.isPublish = false;
    	Message bidUpdateMsg = new Message(this.UUID,bidUpdate);
    	this.outgoing.add(bidUpdateMsg);
    }
    
    public void SubscribeSaleNotice(int itemId){
    	SaleNotice saleNotice = new SaleNotice(itemId, 0, 0.00);
    	saleNotice.isPublish = false;
    	Message saleNoticeMsg = new Message(this.UUID,saleNotice);
    	this.outgoing.add(saleNoticeMsg);
    }

}