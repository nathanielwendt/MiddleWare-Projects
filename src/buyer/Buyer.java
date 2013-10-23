package buyer;
import gui.BuyerGUI;
import includes.UUIDGenerator;

import java.io.IOException;
import java.util.Scanner;
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
	private BuyerGUI guiInstance;
	
    public static void main(String[] args) throws IOException {
    	Buyer buyer = new Buyer(null);
    	Scanner scan = new Scanner(System.in);
    	while(true){
			System.out.println("Enter choice :");
			String input = scan.nextLine();
			if(input.toLowerCase().equals("interestone")){
				buyer.publishInterest("Car",SaleItem.MODIFIER_STRING_IGNORE,SaleItem.TIME_STAMP_IGNORE,1000,SaleItem.COST_UPPER_BOUND_IGNORE);
			}else if(input.toLowerCase().equals("interesttwo")){
				buyer.publishInterest("Car","Mercedes",SaleItem.TIME_STAMP_IGNORE,1000,SaleItem.COST_UPPER_BOUND_IGNORE);
			}else if(input.toLowerCase().equals("bidupdateinterest")){
				buyer.interestBidUpdate(buyer.getCommunicationThread().getMatchingItems().get(0).getUuid());
			}else if(input.toLowerCase().equals("bidauto1000")){
				buyer.publishAutoBid(buyer.getCommunicationThread().getMatchingItems().get(0).getUuid(),100,1000);
			}else if(input.toLowerCase().equals("bid1")){
				buyer.publishBid(buyer.getCommunicationThread().getMatchingItems().get(0).getUuid(),1);
			}else if(input.toLowerCase().equals("bid800")){
					buyer.publishBid(buyer.getCommunicationThread().getMatchingItems().get(0).getUuid(),800);
			}else if(input.toLowerCase().equals("bid1000")){
				buyer.publishBid(buyer.getCommunicationThread().getMatchingItems().get(0).getUuid(),1000);
			} else if(input.toLowerCase().equals("bid1001")){
				buyer.publishBid(buyer.getCommunicationThread().getMatchingItems().get(0).getUuid(),1001);
			}
        }
    }
    
    public Buyer(BuyerGUI guiInstance){
    	this.setUUID(UUIDGenerator.getNextUUID());
    	this.communicationThread = new BuyerIOThread(this.incoming, this.outgoing, this, guiInstance);
    	this.communicationThread.start();
    	this.guiInstance = guiInstance;
    }

    public void printNotices(){
    	Message nextMsg;
    	nextMsg = (Message) this.incoming.poll();
		if(nextMsg != null){
			System.out.println(nextMsg.debugString);		
		}
    	
    }
    
    public BuyerIOThread getCommunicationThread(){
    	return this.communicationThread;
    }
    

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uUID) {
		uuid = uUID;
	}
	
	public void publishAutoBid(String itemUUID, double increment, double maxValue){
		this.getCommunicationThread().setAutoBid(itemUUID,increment,maxValue);
		this.interestBidUpdate(itemUUID); //automatically subscribe to bidupdate events so the auto bidder can work properly
		
		//publish bid
		Bid bid = new Bid(this.uuid,itemUUID,0.0); //0.0 is a dummy value that will be overwritten by the seller since it is an auto min bid
		bid.setAutoMinBid();
		Message message  = new Message("Bid from a buyer",bid,bid.getItemUUID());
		this.outgoing.add(message);
	}
	
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
		if(guiInstance != null){
			guiInstance.addItemToInterestsTable(interest);
		}
		this.communicationThread.getPublishedInterests().add(interest);
		Message message  = new Message("Interest request from a buyer",interest,interest.getUuid());
		this.outgoing.add(message);
	}
	
	public void interestBidUpdate(String itemUUID){
		//these need to be cached to enable receipt of bid updates.
		InterestBidUpdate interest = new InterestBidUpdate(this.uuid,itemUUID);
		Message message  = new Message("Interest bid update from buyer",interest,interest.getItemUUID());
		this.outgoing.add(message);
	}
    
    
}