package brokers;

import includes.EventType;
import includes.LinkName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import setup.Init;
import entities.Message;
import events.Bid;
import events.BidUpdate;
import events.InterestBidUpdate;
import events.SaleFinalized;
import events.SaleItem;

public class BrokerLinkThread extends Thread {
	protected Socket socket = null;
	protected BrokerManager brokerManager = null;
	protected PrintWriter outstream;
	protected BufferedReader instream;

	public BrokerLinkThread(){}

	public BrokerLinkThread(Socket socket, BrokerManager brokerManager, int priority) {
		super("BrokerCommunicateThread");
		this.socket = socket;
		this.brokerManager = brokerManager;
		setPriority(priority); 
	}
	
	protected boolean[] combineBooleanArrays(boolean[] one,boolean[] two){
		boolean[] result = new boolean[one.length];
		for(int i=0; i<result.length;i++){
			result[i] = one[i] || two[i];
		}
		return result;
	}

	public void run() { 
		try {
			this.outstream = new PrintWriter(this.socket.getOutputStream(), true);
			this.instream = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
			String inputLine;

			inputLine = this.instream.readLine();
			if(Init.VERBOSE) {
				System.out.println("New incoming message -> " + inputLine );
			}
			if(inputLine.equals("AttemptConnect::Broker")){
				if(Init.VERBOSE) {
					System.out.println("The incoming connection has been resolved to be a broker.");
				}
				//inform this new broker of the global count information
				int newBrokerCount = Integer.parseInt(this.instream.readLine());
				int newClientCount = Integer.parseInt(this.instream.readLine());
				BrokerDistributor.updateCounts(newBrokerCount, newClientCount);
				resolveListening(brokerManager.getParentLink());
			} else if(inputLine.equals("AttemptConnect::Buyer") || inputLine.equals("AttemptConnect::Seller")) {
				if(Init.VERBOSE) {
					System.out.println("The incoming connection has been resolved to be a client of type " + (inputLine.contains("Buyer") ? "Buyer" : "Seller"));
				}
				BrokerDistributor.incrementClientCount();
				resolveListening(brokerManager.getNextLink());
			} else {
				if(Init.VERBOSE) {
					System.out.println("The credentials used to establish a connection as invalid. The process has been aborted!");
				}
			}
			outstream.close();
			instream.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void resolveListening(LinkName nextLinkName){
		if(Init.VERBOSE) {
			if(nextLinkName != LinkName.forward){
				System.out.println("It has been decided that incoming connection should be linked to " + nextLinkName);
			}else{
				System.out.println("As the current broker is full, it has been decided that the incoming connection will be forwarded to a child broker");
			}
		}
		if(nextLinkName == LinkName.pos0){
			manageConection(brokerManager.pos0,BrokerManager.FIRST_CLIENT_CHILD);
		}
		else if(nextLinkName == LinkName.pos1){
			manageConection(brokerManager.pos1,BrokerManager.SECOND_CLIENT_CHILD);
		}
		else if(nextLinkName == LinkName.createleft){
			int nextBrokerPort = BrokerDistributor.getNextBrokerPort();
			outstream.println("forwarding");
			outstream.println(nextBrokerPort);
			new BrokerLinkThreadChild(brokerManager, nextBrokerPort, nextLinkName).start();
		}
		else if(nextLinkName == LinkName.createright){
			int nextBrokerPort = BrokerDistributor.getNextBrokerPort();
			outstream.println("forwarding");
			outstream.println(nextBrokerPort);
			new BrokerLinkThreadChild(brokerManager, nextBrokerPort, nextLinkName).start();
		}
		else if(nextLinkName == LinkName.forward){
			if(brokerManager.getBrokerId() != 1){ //regular broker
				int childNodePort = BrokerDistributor.getBrokerAtIndex(BrokerDistributor.getDeployedBrokerCount() - 1);
				if(Init.VERBOSE) {
					System.out.println("Current broker is full. The client is being forwarded to the broker on " + childNodePort);
				}
				outstream.println("forwarding");
				outstream.println(childNodePort);
			} else { //Master Broker, perform full port forwarding
				int clientCount = BrokerDistributor.getDeployedClientCount();
				double nodeDest = (double) clientCount;
				nodeDest = Math.ceil(nodeDest / 2.0);
				int nodeDestAsInt  = (int) nodeDest;
				if(Init.VERBOSE) {
					System.out.println("Current broker is full. The client is being forwarded to the broker on " + nodeDestAsInt);
				}
				outstream.println("forwarding");
				outstream.println( BrokerDistributor.getBrokerAtIndex(nodeDestAsInt - 2) ); //2 offset since host isn't included and since indexing starts at 0 but we want it to start at 1
			}
		}
		else if(nextLinkName == LinkName.parent){
			manageConection(brokerManager.parent,BrokerManager.PARENT);
		}
	}



	protected void manageConection(LinkedBlockingQueue<Message> firstChildQueue, int dataSource){
		try{
			outstream.println("Connection Established"); 
			if(Init.VERBOSE) {
				System.out.println("Established connection with " + BrokerManager.getDebugString(dataSource));
			}
			String inputLine = "";
			Message nextMsg;
			while(true){
				//check connected entity for incoming messages
				if(instream.ready()){
					inputLine = instream.readLine();
					if(Init.VERBOSE) {
						System.out.println("New message received from " + BrokerManager.getDebugString(dataSource));
						System.out.println("The message is -> " + inputLine);
					}
					if(inputLine.equals("forwarding")){
						if(Init.VERBOSE) {
							System.out.println("Broker server full, so request forworded to another broker server.");
							inputLine = "";
						}
						break;
					}else if(inputLine.equals("Connection Established")) {
						if(Init.VERBOSE){
							System.out.println("Established persistent connection with a child broker.");
							inputLine = "";
						}
					}else{
						Message receivedMessage = Message.getObjectFromJson(inputLine);
						if(receivedMessage.getEventType() == EventType.saleitem){
							SaleItem item = SaleItem.getObjectFromJson(receivedMessage.getEventAsJson());
							if(item.isInterest()){ //coming from a buyer or a child
								this.brokerManager.parent.add(receivedMessage); //propagate it to the top parent to looked up in the future
								this.brokerManager.getInterestsDB().addItemToDataBase(item);
								receivedMessage.setMessageSourceValue(dataSource, true);
								this.brokerManager.getMessageSource().put(receivedMessage.getUuid(), receivedMessage.getMessageSourceArray().clone());
								receivedMessage.setMessageSourceValue(dataSource, false); //revert the changes to make sure messages on outbox are not affected
								if(Init.VERBOSE) {
									System.out.println("It has been identified as an interest and propagated to the parent.");
								}
							}else{ //coming from a seller or a child, needs to be matched and propagated
								this.brokerManager.parent.add(receivedMessage); //propagate it to the top parent to be matched and dispatched
								this.brokerManager.getAvailableItemDatabase().put(receivedMessage.getUuid(), item);
								receivedMessage.setMessageSourceValue(dataSource, true);
								this.brokerManager.getMessageSource().put(receivedMessage.getUuid(), receivedMessage.getMessageSourceArray().clone());
								receivedMessage.setMessageSourceValue(dataSource, false); //revert the changes to make sure messages on outbox are not affected
								if(Init.VERBOSE) {
									System.out.println("It has been identified as an available item.");
								}
								ArrayList<SaleItem> matches = this.brokerManager.getInterestsDB().getSearchResult(item);
								if(Init.VERBOSE) {
									System.out.println("There are " + matches.size() + " interest matches to the available item in the database.");
								}
								for(SaleItem s : matches){
									boolean[] propagateValues = this.brokerManager.getMessageSource().get(s.getUuid());
									for(int i=0;i<propagateValues.length;i++){
										if(propagateValues[i] && (i != dataSource)){ //send to all interested nodes and avoid duplicates
											this.brokerManager.getQueueAccordingToSource(i).add(receivedMessage);
										}
									}
								}
								if(Init.VERBOSE) {
									System.out.println("The matches have been notified appropriately.");
								}
							}
						}else if(receivedMessage.getEventType() == EventType.interestbidupdate){ //coming from a buyer
							InterestBidUpdate interest = InterestBidUpdate.getObjectFromJson(receivedMessage.getEventAsJson());
							this.brokerManager.parent.add(receivedMessage); //propagate it to the top parent to looked up in the future
							this.brokerManager.getInterestBidUpdates().put(receivedMessage.getUuid(), interest);
							receivedMessage.setMessageSourceValue(dataSource, true);
							//since we will be having a lot of interests on the same item, we need to update the source information, rather than adding new info everytime
							boolean[] existingSourceInfo = this.brokerManager.getBidUpdateSource().get(interest.getItemUUID());
							boolean[] arrayToBeStored = receivedMessage.getMessageSourceArray().clone();
							if(existingSourceInfo != null){
								arrayToBeStored = combineBooleanArrays(receivedMessage.getMessageSourceArray().clone(),existingSourceInfo);
							}
							this.brokerManager.getBidUpdateSource().put(receivedMessage.getUuid(), arrayToBeStored);
							receivedMessage.setMessageSourceValue(dataSource, false); //revert the changes to make sure messages on outbox are not affected
							if(Init.VERBOSE) {
								System.out.println("It has been identified as an interest bid update and propagated to the parent.");
							}
						}else if(receivedMessage.getEventType() == EventType.bid){ //look up available item data and send to that seller as a bid
							Bid bid = Bid.getObjectFromJson(receivedMessage.getEventAsJson());
							this.brokerManager.parent.add(receivedMessage); //propagate it to the top parent to be sent to the right seller
							receivedMessage.setMessageSourceValue(dataSource, true);
							//since we will be having a lot of bids, we need to update the source information, rather than adding new info everytime
							boolean[] existingSourceInfo = this.brokerManager.getMessageSourceClient().get(bid.getItemUUID());
							boolean[] arrayToBeStored = receivedMessage.getMessageSourceArray().clone();
							if(existingSourceInfo != null){
								arrayToBeStored = combineBooleanArrays(receivedMessage.getMessageSourceArray().clone(),existingSourceInfo);
							}
							// make sure the message is put in a different hashmap as the key matches with 
							this.brokerManager.getMessageSourceClient().put(bid.getItemUUID(), arrayToBeStored);
							receivedMessage.setMessageSourceValue(dataSource, false); //revert the changes to make sure messages on outbox are not affected
							//send to the respective seller
							boolean[] propagateValues = this.brokerManager.getMessageSource().get(bid.getItemUUID());
							for(int i=0;i<propagateValues.length;i++){
								if(propagateValues[i] && (i != dataSource)){ //send to all interested nodes and avoid duplicates
									this.brokerManager.getQueueAccordingToSource(i).add(receivedMessage);
								}
							}
							if(Init.VERBOSE) {
								System.out.println("It has been identified as a bid and propagated to the parent to be sent to the seller.");
							}
						}else if(receivedMessage.getEventType() == EventType.bidUpdate){
							BidUpdate update = BidUpdate.getObjectFromJson(receivedMessage.getEventAsJson());
							this.brokerManager.parent.add(receivedMessage); //propagate it to the top parent to be sent to the interested buyers
							//send to the interested buyers
							boolean[] propagateValues = this.brokerManager.getBidUpdateSource().get(update.getItemUUID());
							for(int i=0;i<propagateValues.length;i++){
								if(propagateValues[i] && (i != dataSource)){ //send to all interested nodes and avoid duplicates
									this.brokerManager.getQueueAccordingToSource(i).add(receivedMessage);
								}
							}
							if(Init.VERBOSE) {
								System.out.println("It has been identified as a bid update and propagated to the parent to be sent to the interested buyers.");
							}
						}else if(receivedMessage.getEventType() == EventType.saleNotice){
							SaleFinalized sale = SaleFinalized.getObjectFromJson(receivedMessage.getEventAsJson());
							this.brokerManager.parent.add(receivedMessage); //propagate it to the top parent to be sent to the buyers
							//send to the interested buyers
							boolean[] propagateValues = this.brokerManager.getMessageSourceClient().get(sale.getItemUUID());
							for(int i=0;i<propagateValues.length;i++){
								if(propagateValues[i] && (i != dataSource)){ //send to all interested nodes and avoid duplicates
									this.brokerManager.getQueueAccordingToSource(i).add(receivedMessage);
								}
							}
							if(Init.VERBOSE) {
								System.out.println("It has been identified as a sale finalized notice and sent to all the users who bid on the item.");
							}
						}else{
							if(Init.VERBOSE) {
								System.err.println("Message type not identified.");
							}
						}
					}
				}

				//poll the queue and send them to the destination
				nextMsg = (Message) firstChildQueue.poll();
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

			}
		} catch (IOException e) {
			System.out.println("Socket was closed from the other side");
			e.printStackTrace();
		}
	}

}
