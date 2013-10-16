package masterbroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

import setup.Init;
import entities.BrokerDistributor;
import entities.BrokerManager;
import includes.LinkName;
import entities.Message;

public class BrokerLinkThreadChild extends BrokerLinkThread {
	private LinkName linkName;
	private int nextBrokerPort;
	
	public BrokerLinkThreadChild(BrokerManager brokerManager, int nextBrokerPort, LinkName linkName){
		this.linkName = linkName;
	    this.brokerManager = brokerManager;
	    this.nextBrokerPort = nextBrokerPort;
        try {
            this.socket = new Socket(Init.NETWORKNAME, nextBrokerPort);
        } catch (IOException e) {
            System.err.println("Don't know about host: " + nextBrokerPort);
            System.exit(1);
        }
	}
	
	public void run() { 
	    try {
	        this.outstream = new PrintWriter(this.socket.getOutputStream(), true);
	        this.instream = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
	        
	        if(this.linkName == LinkName.createleft)
	        	setConnection(brokerManager.left);
	        else
	        	setConnection(brokerManager.right);

	        outstream.close();
	        instream.close();
	        socket.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
	
	protected void setConnection(LinkedBlockingQueue<Message> bq){
		outstream.println("AttemptConnect::Broker");
		outstream.println(BrokerDistributor.getDeployedBrokerCount());
		outstream.println(BrokerDistributor.getDeployedClientCount());
		super.setConnection(bq);
	}
}
