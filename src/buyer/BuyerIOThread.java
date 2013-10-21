package buyer;

import includes.EventType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import setup.Init;
import entities.Message;
import events.BidUpdate;
import events.SaleFinalized;
import events.SaleItem;

public class BuyerIOThread extends Thread {
	private Socket socket = null;
	private PrintWriter outstream = null;
	private BufferedReader instream = null;
	private String networkName = Init.NETWORKNAME;
	private int hostPort = Init.MASTERPORT;
	private LinkedBlockingQueue<Message> incoming = null;
	private LinkedBlockingQueue<Message> outgoing = null;
	
	
	private ArrayList<SaleItem> matchingItems;
	private ArrayList<SaleItem> publishedInterests;
	private ArrayList<BidUpdate> updatesReceived;
	private ArrayList<SaleFinalized> finalizedSales;


	public BuyerIOThread(LinkedBlockingQueue<Message> incoming, LinkedBlockingQueue<Message> outgoing){
		this.incoming = incoming;
		this.outgoing = outgoing;
		this.matchingItems = new ArrayList<SaleItem>();
		this.publishedInterests = new ArrayList<SaleItem>();
		this.updatesReceived = new ArrayList<BidUpdate>();
		this.setFinalizedSales(new ArrayList<SaleFinalized>());
	}

	public void run() {   
		int nextPort = this.hostPort;		
		//will keep going down the tree of broker connections, until it finds an empty spot
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
		this.outstream.println("AttemptConnect::Buyer");
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
					}else{// means its a proper message
						Message receivedMessage = Message.getObjectFromJson(fromServer);
						if(receivedMessage != null){
							if(Init.VERBOSE) {
								System.out.println("New message received from the broker.");
							}
							if(receivedMessage.getEventType() == EventType.saleitem){
								SaleItem item = SaleItem.getObjectFromJson(receivedMessage.getEventAsJson());
								if(!item.isInterest()){
									this.matchingItems.add(item); //add item to matchingItems for future reference
									if(Init.VERBOSE) {
										System.out.println("The message has been identified to be an available item.");
										System.out.println("The item is -> " + item.toJson());
									}
								}else{
									System.err.println("Something wrong. A buyer is not supposed to receive an interest message! Message received -> " + item.toJson());
								}
							}else if(receivedMessage.getEventType() == EventType.bidUpdate){
								BidUpdate update = BidUpdate.getObjectFromJson(receivedMessage.getEventAsJson());
								this.updatesReceived.add(update);
								if(Init.VERBOSE) {
									System.out.println("The message has been identified to be a bid update.");
									System.out.println("The update is -> " + update.toJson());
								}
							}else if(receivedMessage.getEventType() == EventType.saleNotice){
								SaleFinalized finalSale = SaleFinalized.getObjectFromJson(receivedMessage.getEventAsJson());
								this.finalizedSales.add(finalSale);
								if(Init.VERBOSE) {
									System.out.println("The message has been identified to be a finalized sale notification.");
									System.out.println("The finalsale is -> " + finalSale.toJson());
								}
							}else{
								System.err.println("Buyer just received an unsupported message.Message -> " + receivedMessage.toJson());
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

	public ArrayList<SaleItem> getPublishedInterests() {
		return publishedInterests;
	}

	public void setPublishedInterests(ArrayList<SaleItem> publishedInterests) {
		this.publishedInterests = publishedInterests;
	}

	public ArrayList<SaleItem> getMatchingItems() {
		return matchingItems;
	}

	public void setMatchingItems(ArrayList<SaleItem> matchingItems) {
		this.matchingItems = matchingItems;
	}

	public ArrayList<BidUpdate> getUpdatesReceived() {
		return updatesReceived;
	}

	public void setUpdatesReceived(ArrayList<BidUpdate> updatesReceived) {
		this.updatesReceived = updatesReceived;
	}

	public ArrayList<SaleFinalized> getFinalizedSales() {
		return finalizedSales;
	}

	public void setFinalizedSales(ArrayList<SaleFinalized> finalizedSales) {
		this.finalizedSales = finalizedSales;
	}
}
