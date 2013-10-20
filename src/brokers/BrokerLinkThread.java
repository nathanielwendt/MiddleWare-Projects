package brokers;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

import setup.Init;

import entities.Message;
import includes.LinkName;

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
 
    public void run() { 
	    try {
	        this.outstream = new PrintWriter(this.socket.getOutputStream(), true);
	        this.instream = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
	        String inputLine;
	        
	        inputLine = this.instream.readLine();
	        System.out.println("Detected a: " + inputLine);
	        if(inputLine.equals("AttemptConnect::Broker")){
	        	//inform this new broker of the global count information
	        	int newBrokerCount = Integer.parseInt(this.instream.readLine());
	        	int newClientCount = Integer.parseInt(this.instream.readLine());
	        	BrokerDistributor.updateCounts(newBrokerCount, newClientCount);
	        	//brokerManager.setBrokerId(newBrokerCount);
	        	resolveListening(brokerManager.getParentLink());
	        } else if(inputLine.equals("AttemptConnect::Buyer") || inputLine.equals("AttemptConnect::Seller")) {
	        	BrokerDistributor.incrementClientCount();
	        	System.out.println("that must be a client");
	        	resolveListening(brokerManager.getNextLink());
	        } else {
	        	System.out.println("invalid connection credentials, disconnecting");
	        }
	        outstream.close();
	        instream.close();
	        socket.close();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
    
    protected void resolveListening(LinkName nextLinkName){
    	System.out.println(nextLinkName);
		if(nextLinkName == LinkName.pos0){
			manageConectionWithFirstClientChild();
		}
		else if(nextLinkName == LinkName.pos1){
			manageConectionWithSecondClientChild();
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
				System.out.println("fowrad to: " + childNodePort);
				outstream.println("forwarding");
				outstream.println(childNodePort);
			} else { //Master Broker, perform full port forwarding
				int clientCount = BrokerDistributor.getDeployedClientCount();
				double nodeDest = (double) clientCount;
				nodeDest = Math.ceil(nodeDest / 2.0);
				int nodeDestAsInt  = (int) nodeDest;
				System.out.println("forward to: " + nodeDestAsInt);
				outstream.println("forwarding");
				outstream.println( BrokerDistributor.getBrokerAtIndex(nodeDestAsInt - 2) ); //2 offset since host isn't included and since indexing starts at 0 but we want it to start at 1
			}
		}
		else if(nextLinkName == LinkName.parent){
			manageConectionWithParent();
		}
    }
    
    
    
    protected void manageConectionWithRightChildBroker(){
    	LinkedBlockingQueue<Message> rightChildBroker = brokerManager.left;
    	try{
    		outstream.println("Connection Established"); // this will write to the parent
    		String inputLine = "";
    		Message nextMsg;
    		while(true){
    			
    			//check connected entity for incoming messages
    			if(instream.ready()){
    				inputLine = instream.readLine();
    				Message testmsg = new Message(inputLine);
    				rightChildBroker.add(testmsg);
    			}
    			
	
    			//check for new events or subscription that should be made known to the entity
    			nextMsg = (Message) rightChildBroker.poll();
				if(nextMsg != null){
					//inform the socket connection of the message
					
					//demo code, in the future this should be a serialized object
					outstream.println(nextMsg.debugString);		
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
    
    
    
    
    protected void manageConectionWithLeftChildBroker(){
    	LinkedBlockingQueue<Message> leftChildBroker = brokerManager.left;
    	try{
    		outstream.println("Connection Established"); // this will write to the parent
    		String inputLine = "";
    		Message nextMsg;
    		while(true){
    			
    			//check connected entity for incoming messages
    			if(instream.ready()){
    				inputLine = instream.readLine();
    				Message testmsg = new Message(inputLine);
    				leftChildBroker.add(testmsg);
    			}
    			
	
    			//check for new events or subscription that should be made known to the entity
    			nextMsg = (Message) leftChildBroker.poll();
				if(nextMsg != null){
					//inform the socket connection of the message
					
					//demo code, in the future this should be a serialized object
					outstream.println(nextMsg.debugString);		
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
    
    
    
    
    protected void manageConectionWithSecondClientChild(){
    	LinkedBlockingQueue<Message> secondChildQueue = brokerManager.pos1;
    	try{
    		outstream.println("Connection Established"); // this will write to the parent
    		String inputLine = "";
    		Message nextMsg;
    		while(true){
    			
    			//check connected entity for incoming messages
    			if(instream.ready()){
    				inputLine = instream.readLine();
    				Message testmsg = new Message(inputLine);
    				secondChildQueue.add(testmsg);
    			}
    			
	
    			//check for new events or subscription that should be made known to the entity
    			nextMsg = (Message) secondChildQueue.poll();
				if(nextMsg != null){
					//inform the socket connection of the message
					
					//demo code, in the future this should be a serialized object
					outstream.println(nextMsg.debugString);		
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
    
    
    
    
    protected void manageConectionWithFirstClientChild(){
    	LinkedBlockingQueue<Message> firstChildQueue = brokerManager.pos0;
    	try{
    		outstream.println("Connection Established"); // this will write to the parent
    		String inputLine = "";
    		Message nextMsg;
    		while(true){
    			
    			//check connected entity for incoming messages
    			if(instream.ready()){
    				inputLine = instream.readLine();
    				Message testmsg = new Message(inputLine);
    				firstChildQueue.add(testmsg);
    			}
    			
	
    			//check for new events or subscription that should be made known to the entity
    			nextMsg = (Message) firstChildQueue.poll();
				if(nextMsg != null){
					//inform the socket connection of the message
					
					//demo code, in the future this should be a serialized object
					outstream.println(nextMsg.debugString);		
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
    
    
    
    
    protected void manageConectionWithParent(){
    	LinkedBlockingQueue<Message> parentQueue = brokerManager.parent;
    	try{
    		outstream.println("Connection Established"); // this will write to the parent
    		String inputLine = "";
    		Message nextMsg;
    		while(true){
    			
    			//check connected entity for incoming messages
    			if(instream.ready()){
    				inputLine = instream.readLine();
    				Message testmsg = new Message(inputLine);
    				parentQueue.add(testmsg);
    			}
    			
	
    			//check for new events or subscription that should be made known to the entity
    			nextMsg = (Message) parentQueue.poll();
				if(nextMsg != null){
					//inform the socket connection of the message
					
					//demo code, in the future this should be a serialized object
					outstream.println(nextMsg.debugString);		
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
