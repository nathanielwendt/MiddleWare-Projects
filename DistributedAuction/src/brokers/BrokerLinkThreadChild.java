//@file BrokerLinkThreadChild.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ Thread responsible for handling interaction with other brokers

package brokers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import setup.Init;
import includes.LinkName;

public class BrokerLinkThreadChild extends BrokerLinkThread {
	private LinkName linkName;
	
	// Useful Constructor
	//@params socket - socket the caller has passed to interact with some entity
	//@params nextBrokerPort - broker port to connect to
	//@params linkName - name of link connection (left broker child or right broker child)
	public BrokerLinkThreadChild(BrokerManager brokerManager, int nextBrokerPort, LinkName linkName){
		this.linkName = linkName;
	    this.brokerManager = brokerManager;
        try {
            this.socket = new Socket(Init.NETWORKNAME, nextBrokerPort);
            if(Init.VERBOSE) {
            	System.out.println("Master broker connected to child broker on port " + nextBrokerPort + " due to forwarding!" );
            }
        } catch (IOException e) {
            System.err.println("Don't know about host: " + nextBrokerPort);
            System.exit(1);
        }
	}
	
	// Master method called on thread when created
	// Connects to the entity and establishes it's identity and directs
	// this threads treatment of it based on it's identity.
	public void run() { 
	    try {
	        this.outstream = new PrintWriter(this.socket.getOutputStream(), true);
	        this.instream = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
	        
	        outstream.println("AttemptConnect::Broker");
    		outstream.println(BrokerDistributor.getDeployedBrokerCount());
    		outstream.println(BrokerDistributor.getDeployedClientCount());
    		
	        if(this.linkName == LinkName.createleft){
	        	manageConection(brokerManager.left,BrokerManager.LEFT_BROKER_CHILD);
	        }else{
	        	manageConection(brokerManager.right,BrokerManager.RIGHT_BROKER_CHILD);
	        }
	        outstream.close();
	        instream.close();
	        socket.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
	
}
