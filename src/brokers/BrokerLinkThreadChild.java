package brokers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

import setup.Init;
import includes.LinkName;
import entities.Message;

public class BrokerLinkThreadChild extends BrokerLinkThread {
	private LinkName linkName;
	
	public BrokerLinkThreadChild(BrokerManager brokerManager, int nextBrokerPort, LinkName linkName){
		this.linkName = linkName;
	    this.brokerManager = brokerManager;
        try {
            this.socket = new Socket(Init.NETWORKNAME, nextBrokerPort);
            if(Init.VERBOSE) {
            	System.out.println("Master broker connected to child broker on port -> " + nextBrokerPort + " due to forwarding!" );
            }
        } catch (IOException e) {
            System.err.println("Don't know about host: " + nextBrokerPort);
            System.exit(1);
        }
	}
	
	public void run() { 
	    try {
	        this.outstream = new PrintWriter(this.socket.getOutputStream(), true);
	        this.instream = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
	        
	        outstream.println("AttemptConnect::Broker");
    		outstream.println(BrokerDistributor.getDeployedBrokerCount());
    		outstream.println(BrokerDistributor.getDeployedClientCount());
    		
	        if(this.linkName == LinkName.createleft){
	        	manageConectionWithLeftChildBroker();
	        }else{
	        	manageConectionWithRightChildBroker();
	        }
	        outstream.close();
	        instream.close();
	        socket.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
	
}
