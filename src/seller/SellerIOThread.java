package seller;

import includes.EventType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import setup.Init;
import entities.Message;
import events.Bid;
import events.BidUpdate;
import events.SaleItem;

public class SellerIOThread extends Thread {
	private Socket socket = null;
	private PrintWriter outstream = null;
	private BufferedReader instream = null;
	private String networkName = Init.NETWORKNAME;
	private int hostPort = Init.MASTERPORT;
	private LinkedBlockingQueue<Message> incoming = null;
	private LinkedBlockingQueue<Message> outgoing = null;
	private Seller sellerInstance;

	private ArrayList<Bid> bidsReceived;
	//private ArrayList<SaleItem> publishedAvailableItems;
	private HashMap<String,SaleItem> publishedAvailableItems; //changed to hashmap for easy lookup
	private HashMap<String,Bid> bidLeaders; //maintains information about current highest bidders for each saleItem, String is UUID

	public SellerIOThread(Seller sellerInstance,LinkedBlockingQueue<Message> incoming, LinkedBlockingQueue<Message> outgoing){
		this.incoming = incoming;
		this.outgoing = outgoing;
		this.setBidsReceived(new ArrayList<Bid>());
		this.setBidLeaders(new HashMap<String,Bid>());
		this.setPublishedAvailableItems(new HashMap<String,SaleItem>());
		this.sellerInstance = sellerInstance;
	}

	public void run() {   
		int nextPort = this.hostPort;		
		//will settle on loopOnBroker once connection is appropriate
		while(true){
			connectToBroker(nextPort);
			nextPort = loopOnBroker();
			closeOldConnection();
		}
	}

	public void connectToBroker(int nextPort){
		try {
			this.socket = new Socket(this.networkName, nextPort);
			this.outstream = new PrintWriter(this.socket.getOutputStream(), true);
			this.instream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + this.hostPort);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: Broker");
			System.exit(1);
		}
	}

	//returns the next port if loopOnBroker is closed and forwarded
	public int loopOnBroker(){
		String fromServer = "";
		int nextPort;
		//protocol to identify buyer to the server
		this.outstream.println("AttemptConnect::Seller");
		while(true) {
			try {
				if(this.instream.ready()){
					fromServer = this.instream.readLine();
					if(fromServer.equals("forwarding")){
						if(Init.VERBOSE) {
							System.out.println("Broker server full, so request forworded to another broker server.");
							fromServer = "";
						}
						break;
					}else if(fromServer.equals("Connection Established")) {
						if(Init.VERBOSE){
							System.out.println("Established persistent connection with a broker server.");
							fromServer = "";
						}
					}else{ //means its a proper message
						Message receivedMessage = Message.getObjectFromJson(fromServer);
						if(receivedMessage != null){
							if(Init.VERBOSE) {
								System.out.println("New message received from the broker.");
							}
							if(receivedMessage.getEventType() == EventType.bid){
								Bid receivedBid = Bid.getObjectFromJson(receivedMessage.getEventAsJson());
								//this.bidsReceived.add(receivedBid);
								if(Init.VERBOSE) {
									System.out.println("The message has been identified to be a bid from a buyer.");
									System.out.println("The bid is -> " + receivedBid.toJson());
								}
								

								//implemented logic figure out whether to publish or not
								boolean isMaxBid = this.updateMaxBid(receivedBid);
								if(isMaxBid){
									BidUpdate update = new BidUpdate(receivedBid.getBidderUUID(),receivedBid.getItemUUID(),receivedBid.getBidValue());
									Message toBePublished = new Message("Bid update for an item",update,update.getItemUUID());
									this.outgoing.add(toBePublished);
									if(Init.VERBOSE) {
										System.out.println("The bid update for the received bid has been published.");
									}
								}
							}else{
								System.err.println("Seller just received an unsupported message.Message -> " + receivedMessage.toJson());
							}

						}
					}
				}
				//Send all the messages that need to be sent from the outgoing queue
				Message nextMsg = (Message) outgoing.poll();
				if(nextMsg != null){
					outstream.println(nextMsg.toJson());		
				}


				if(Init.ENABLE_THREAD_SLEEP){
					try{
						Thread.sleep(Init.THREAD_SLEEP_INTERVAL); 
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}

			} catch (IOException e){
				e.printStackTrace();
			}
		}
		try {
			fromServer = this.instream.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
		nextPort = Integer.parseInt(fromServer);
		if(Init.VERBOSE){
			System.out.println("The next proposed broker server is on port " + nextPort);
		}
		return nextPort;
	}


	/** Garbage collect connections which are not of use anymore */
	public void closeOldConnection(){
		try {
			this.socket.close();
			this.outstream.close();
			this.instream.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public ArrayList<Bid> getBidsReceived() {
		return bidsReceived;
	}

	public void setBidsReceived(ArrayList<Bid> bidsReceived) {
		this.bidsReceived = bidsReceived;
	}

	//changed to hashmap
	public HashMap<String,SaleItem> getPublishedAvailableItems() {
		return publishedAvailableItems;
	}

	//changed to hashmap
	public void setPublishedAvailableItems(HashMap<String,SaleItem> publishedAvailableItems) {
		this.publishedAvailableItems = publishedAvailableItems;
	}
	
	public void setBidLeaders(HashMap<String,Bid> bidLeaders){
		this.bidLeaders = bidLeaders;
	}
	
	//returns true if bid is updated
	public boolean updateMaxBid(Bid receivedBid){
		Bid currentBidLeader = this.bidLeaders.get(receivedBid.getItemUUID()); //look up in the hashmap the associated bid
		SaleItem biddingItem = this.publishedAvailableItems.get(receivedBid.getItemUUID());  //look up the associated published available item
		boolean bidIsAboveMinimum = (biddingItem.getCostLowerBound() < receivedBid.getBidValue());
		if(currentBidLeader == null){ //first bid on item
			if(bidIsAboveMinimum){
				this.bidLeaders.put(receivedBid.getItemUUID(), receivedBid);
				return true;
			} else {
				return false;
			}
		} else if(currentBidLeader.getBidValue() < receivedBid.getBidValue()){
			this.bidLeaders.put(receivedBid.getItemUUID(), receivedBid);
			return true;
		} else {
			return false;
		}
	}
	
}
