package brokers;
import includes.LinkName;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import database.InterestsDatabase;
import entities.Message;
import events.InterestBidUpdate;
import events.SaleItem;

public class BrokerManager {
	private int numBrokers = 1; //including this broker!
	private int numClients = 0;
	private boolean hasLeftChild = false;
	private boolean hasRightChild = false;
	public int brokerId;

	private InterestsDatabase interestsDB; //used to store interests received from buyer
	private HashMap<String,boolean[]> messageSource; //used to store the source of messages, so that it can be used to send appropriate messages.
	private HashMap<String,SaleItem> availableItemDatabase; //used to keep track of the items sold by sellers
	private HashMap<String,InterestBidUpdate> interestBidUpdates; //used to keep track of interests in bid updates
	
	public static final int LEFT_BROKER_CHILD = 0;
	public static final int RIGHT_BROKER_CHILD = 1;
	public static final int FIRST_CLIENT_CHILD = 2;
	public static final int SECOND_CLIENT_CHILD = 3;
	public static final int PARENT = 4;

	//broker children
	public LinkedBlockingQueue<Message> left = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> right = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> parent  = new LinkedBlockingQueue<Message>();
	//seller/buyer clients
	public LinkedBlockingQueue<Message> pos0 = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> pos1 = new LinkedBlockingQueue<Message>();

	public BrokerManager(int brokerId){
		this.brokerId = brokerId;
		this.setInterestsDB(new InterestsDatabase());
		this.messageSource = new HashMap<String,boolean[]>();
		this.setAvailableItemDatabase(new HashMap<String,SaleItem>());
		this.interestBidUpdates = new HashMap<String,InterestBidUpdate>();
	}

	public int getBrokerId(){
		return this.brokerId;
	}

	public synchronized LinkName getParentLink(){
		this.numBrokers++;
		return LinkName.parent;
	}

	//get Next Link for Client
	public synchronized LinkName getNextLink(){
		int currClientCount = this.numClients + 1; //what the next client count will be
		if (this.numClients == 0){
			this.numClients++;
			return LinkName.pos0;
		}
		else if(this.numClients == 1){
			this.numClients++;
			return LinkName.pos1;
		}else { //no more room on this broker, must point to another broker
			if(this.numBrokers == 1 && !this.hasLeftChild){
				this.numBrokers++;
				this.numClients++;
				this.hasLeftChild = true;
				return LinkName.createleft;
			}else if(this.numBrokers == 2 && !this.hasRightChild && currClientCount == 5){
				this.numBrokers++;
				this.numClients++;
				this.hasRightChild = true;
				return LinkName.createright;
			}else { //not within the scope of this broker or any of its children, pass along by socket and forward client
				if(currClientCount < 6){
					this.numClients++;
				}
				return LinkName.forward;
			}
		}
	}

	public InterestsDatabase getInterestsDB() {
		return interestsDB;
	}

	public void setInterestsDB(InterestsDatabase interestsDB) {
		this.interestsDB = interestsDB;
	}

	public HashMap<String,boolean[]> getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(HashMap<String,boolean[]> messageSource) {
		this.messageSource = messageSource;
	}

	public HashMap<String,SaleItem> getAvailableItemDatabase() {
		return availableItemDatabase;
	}

	public void setAvailableItemDatabase(HashMap<String,SaleItem> availableItemDatabase) {
		this.availableItemDatabase = availableItemDatabase;
	}

	public LinkedBlockingQueue<Message> getQueueAccordingToSource(int dataSource){
		switch(dataSource){
		case LEFT_BROKER_CHILD:
			return left;
		case RIGHT_BROKER_CHILD:
			return right;
		case FIRST_CLIENT_CHILD:
			return pos0;
		case SECOND_CLIENT_CHILD:
			return pos1;
		case PARENT:
			return parent;
		default:
			return null;
		}
	}

	public static String getDebugString(int dataSource){
		switch(dataSource){
		case LEFT_BROKER_CHILD:
			return "the left child broker.";
		case RIGHT_BROKER_CHILD:
			return "the right child broker.";
		case FIRST_CLIENT_CHILD:
			return "the first client child.";
		case SECOND_CLIENT_CHILD:
			return "the second client child.";
		case PARENT:
			return "the parent broker.";
		default:
			return "some unknown host";
		}
	}

	public HashMap<String,InterestBidUpdate> getInterestBidUpdates() {
		return interestBidUpdates;
	}

	public void setInterestBidUpdates(HashMap<String,InterestBidUpdate> interestBidUpdates) {
		this.interestBidUpdates = interestBidUpdates;
	}

	/*
	public synchronized void incrementBrokerCount(){
		++this.numBrokers;
	}

	public synchronized void incrementClientCount(){
		++this.numClients;
	}		

	public synchronized int getBrokerCount(){
		return this.numBrokers;
	}

	public synchronized int getClientCount(){
		return this.numClients;
	}

	public synchronized void setBrokerId(int newId){
		this.brokerId = newId;
	}

	 */
}
