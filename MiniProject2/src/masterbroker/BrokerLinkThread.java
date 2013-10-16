package masterbroker;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

import entities.BrokerDistributor;
import entities.BrokerManager;
import entities.Event;
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
			setConnection(brokerManager.pos0);
		}
		else if(nextLinkName == LinkName.pos1){
			setConnection(brokerManager.pos1);
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
			setConnection(brokerManager.parent);
		}
    }
    
    protected void setConnection(LinkedBlockingQueue<Message> bq){
    	try{
    		outstream.println("Connection Established");
    		String inputLine = "";
    		Message nextMsg;
    		while(true){
    			
    			//check connected entity for incoming messages
    			if(instream.ready()){
    				inputLine = instream.readLine();
    				System.out.println(inputLine);

    				if(inputLine.equals("event")){
        				Message msg = new Message("event from passed");
        				if(bq != brokerManager.parent)
        					brokerManager.parent.add(msg);
    				}
    				//Message tester = new Message("try this out");
    				//this.brokerManager.pos1.add(tester);
    			}
	
    			//check for new events or subscription that should be made known to the entity
    			nextMsg = (Message) bq.poll();
				if(nextMsg != null){
					//inform the socket connection of the message
					
					//demo code, in the future this should be a serialized object
					outstream.println(nextMsg.debugString);		
				}
    		}
    	} catch (IOException e) {
    		System.out.println("Socket was closed from the other side");
    		e.printStackTrace();
    	}
    }
}
