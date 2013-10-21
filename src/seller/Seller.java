package seller;
import includes.UUIDGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
import events.BidUpdate;
import events.SaleFinalized;
import events.SaleItem;
 
public class Seller {
	private String uuid;
	private LinkedBlockingQueue<Message> incoming = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Message> outgoing = new LinkedBlockingQueue<Message>();
	private SellerIOThread communicationThread;
	private ArrayList<SaleItem> publishedAvailableItems;
	
    public static void main(String[] args) throws IOException {
 
    	Seller seller = new Seller();
    	Calendar c = Calendar.getInstance();
    	c.set(Calendar.YEAR, 2009);
    	seller.publishAvailableItem("Car","Mercedes",c.getTimeInMillis(),100,50000);
        while(true){
			try{
				Thread.sleep(3000);
				seller.printNotices();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
        }
    }
    
    public Seller(){
    	this.setUuid(UUIDGenerator.getNextUUID());
    	this.communicationThread = new SellerIOThread(this.incoming, this.outgoing);
    	this.communicationThread.start();
    	this.publishedAvailableItems = new ArrayList<SaleItem>();
    }
    
    public void printNotices(){
    	Message nextMsg;
    	nextMsg = (Message) this.incoming.poll();
		if(nextMsg != null){
			System.out.println(nextMsg.debugString);		
		}
    	
    }
    
    //bid update,sale finalized,available item
    
    public void publishSaleFinalized(String buyerUUID,String itemUUID,double bidValue){
    	SaleFinalized update = new SaleFinalized(buyerUUID,itemUUID,bidValue);
    	// this update is not going to be stored anywhere.
    	Message message = new Message("Bid update from a seller",update,update.getItemUUID());
    	this.outgoing.add(message);
    }
    
    public void publishBidUpdate(String bidderUUID,String itemUUID,double bidValue){
    	BidUpdate update = new BidUpdate(bidderUUID,itemUUID,bidValue);
    	// this update is not going to be stored anywhere.
    	Message message = new Message("Bid update from a seller",update,update.getItemUUID());
    	this.outgoing.add(message);
    }
    
    public void publishAvailableItem(String baseString,String modifierString,long timeStamp,double minimumCost,double maximumCost){
		//need to be looked up on the interest database and cached everywhere in a hashmap
		SaleItem item = new SaleItem(baseString,modifierString,timeStamp,minimumCost,maximumCost,this.uuid);
		item.setInterest(false);
		this.publishedAvailableItems.add(item);
		Message message  = new Message("Available item from a seller",item,item.getUuid());
		this.outgoing.add(message);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}