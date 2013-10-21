package buyer;
import includes.UUIDGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
import events.Bid;
import events.InterestBidUpdate;
import events.SaleItem;
 
public class Buyer {
	private String uuid;
	private LinkedBlockingQueue<Message> incoming = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Message> outgoing = new LinkedBlockingQueue<Message>();
	private BuyerIOThread communicationThread;
	private ArrayList<SaleItem> publishedInterests;
	
    public static void main(String[] args) throws IOException {
    	Buyer buyer = new Buyer();
    	buyer.publishInterest("Car",SaleItem.MODIFIER_STRING_IGNORE,SaleItem.TIME_STAMP_IGNORE,1000,SaleItem.COST_UPPER_BOUND_IGNORE);
        while(true){
			try{
				Thread.sleep(3000);
				buyer.printNotices();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
        }
    }
    
    public Buyer(){
    	this.setUUID(UUIDGenerator.getNextUUID());
    	this.communicationThread = new BuyerIOThread(this.incoming, this.outgoing);
    	this.communicationThread.start();
    	this.publishedInterests = new ArrayList<SaleItem>();
    }
    
    
    
    public void printNotices(){
    	Message nextMsg;
    	nextMsg = (Message) this.incoming.poll();
		if(nextMsg != null){
			System.out.println(nextMsg.debugString);		
		}
    	
    }
    

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uUID) {
		uuid = uUID;
	}
	
	//publish bid,interest,interest bid update
	
	public void publishBid(String itemUUID,double bidValue){
		Bid bid = new Bid(this.uuid,itemUUID,bidValue);
		//the item UUID has been put just as a dummy variable as we are not really caching bids. Incase we decide 
		// to cache bids in the future, bids will have their own UUIDs.
		Message message  = new Message("Bid from a buyer",bid,bid.getItemUUID());
		this.outgoing.add(message);
	}
	
	public void publishInterest(String baseString,String modifierString,long timeStamp,double minimumCost,double maximumCost){
		//need to be cached in the interest database
		SaleItem interest = new SaleItem(baseString,modifierString,timeStamp,minimumCost,maximumCost,this.uuid);
		interest.setInterest(true);
		this.publishedInterests.add(interest);
		Message message  = new Message("Interest request from a buyer",interest,interest.getUuid());
		this.outgoing.add(message);
	}
	
	public void interestBidUpdate(String itemUUID){
		//these need to be cached on the broker the buyer is connected to, just check before forwarding to the buyer, incase
		// a bid comes along.
		InterestBidUpdate interest = new InterestBidUpdate(this.uuid,itemUUID);
		Message message  = new Message("Interest bid update from buyer",interest,interest.getRequestUUID());
		this.outgoing.add(message);
	}
    
    
}