package seller;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
import events.*;
 
public class Seller {
	public String UUID;
	public LinkedBlockingQueue<Message> incoming = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> outgoing = new LinkedBlockingQueue<Message>();
	
    public static void main(String[] args) throws IOException {
 
    	Seller seller = new Seller();
    	new SellerIOThread(seller.incoming, seller.outgoing).start();
        while(true){
			try{
				Thread.sleep(3000);
				seller.PrintNotices();
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
    
    //To-Do adapt SaleItem to fit this style, and reformat this method
    public void PublishSaleItem(String baseString,String modifierString,long timeStamp,double costLowerBound, double costUpperBound){
    	//SaleItem saleItem = new SaleItem(baseString,modifierString,timeStamp,costLowerBound,costUpperBound);
    	//saleItem.isPublish = true;
    	//Message saleNotice = new Message(this.UUID,saleItem);
    	//this.outgoing.add(saleNotice);
    }
    
    public void PublishBidUpdate(int itemId, double bidUpdatePrice){
    	BidUpdate bidUpdate = new BidUpdate(itemId,bidUpdatePrice);
    	bidUpdate.isPublish = true;
    	Message bidUpdateMsg = new Message(this.UUID,bidUpdate);
    	this.outgoing.add(bidUpdateMsg);
    }
    
    public void PublishSaleNotice(int itemId, int buyerId, double saleValue){
    	SaleNotice saleNotice = new SaleNotice(itemId, buyerId, saleValue);
    	saleNotice.isPublish = true;
    	Message saleNoticeMsg = new Message(this.UUID,saleNotice);
    	this.outgoing.add(saleNoticeMsg);
    }
    
    public void SubscribeBid(int itemId){
    	Bid bid = new Bid(itemId, 0.00);
    	bid.isPublish = false;
    	Message bidMsg = new Message(this.UUID,bid);
    	this.outgoing.add(bidMsg);
    }

}