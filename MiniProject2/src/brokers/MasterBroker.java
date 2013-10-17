package brokers;

import java.net.*;
import java.io.*;
import setup.Init;
import entities.*;

public class MasterBroker {
	
	public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
        
        //To-Do fix this line, BrokerDistributor won't have any values in it on a new machine that holds a broker
        BrokerManager brokerManager = new BrokerManager(BrokerDistributor.getDeployedBrokerCount());
 
        try {
            serverSocket = new ServerSocket(Init.MASTERPORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + Init.MASTERPORT);
            System.exit(-1);
        }
 
        int i = 1;
        while(listening){
        	new BrokerLinkThread(serverSocket.accept(), brokerManager, i++).start();
        	//Message tester = new Message("message for first left child, when he/she is ready");
        	//brokerManager.left.add(tester);
        }
        serverSocket.close();
	}
}
