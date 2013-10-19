package brokers;
import java.util.concurrent.*;

import entities.Message;
import includes.LinkName;

public class BrokerManager {
	private int numBrokers = 1;
	private int numClients = 0;
	private boolean hasLeftChild = false;
	private boolean hasRightChild = false;
	public int brokerId;
	
	public LinkedBlockingQueue<Message> left = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> right = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> parent  = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> pos0 = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> pos1 = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> subs = new LinkedBlockingQueue<Message>();
	
	public BrokerManager(int brokerId){
		this.brokerId = brokerId;
	}
	
	public int getBrokerId(){
		return this.brokerId;
	}
	
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
	
	public synchronized LinkName getParentLink(){
		this.numBrokers++;
		return LinkName.parent;
	}
	
	public synchronized void setBrokerId(int newId){
		this.brokerId = newId;
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
		}
		else { //no more room on this broker, must point to another broker
			if(this.numBrokers == 1 && !this.hasLeftChild){
				this.numBrokers++;
				this.numClients++;
				this.hasLeftChild = true;
				return LinkName.createleft;
			}
			else if(this.numBrokers == 2 && !this.hasRightChild && currClientCount == 5){
				this.numBrokers++;
				this.numClients++;
				this.hasRightChild = true;
				return LinkName.createright;
			}
			else { //not within the scope of this broker or any of its children, pass along by socket and forward client
				if(currClientCount < 6)
					this.numClients++;
				return LinkName.forward;
			}
		}
	}
}
