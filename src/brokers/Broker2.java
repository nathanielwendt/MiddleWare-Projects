//@file Broker2.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ Broker1 Implements an ancillary broker that sets up a server at a known port location specified in events.init.java;
//@pre MasterBroker must be running

package brokers;

import java.net.*;
import java.io.*;
import setup.Init;

public class Broker2 {
	
	// Main function creates a server socket and listens on the given port
	// Allocates a new BrokerLinkThread to listen to each incoming connection
	public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
        
        BrokerManager brokerManager = new BrokerManager(1);
 
        try {
            serverSocket = new ServerSocket(Init.BROKERINDEX[1]);
            if(Init.VERBOSE) {
            	System.out.println("Started another broker and listening on port number " + Init.BROKERINDEX[1]);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + Init.BROKERINDEX[1]);
            System.exit(-1);
        }
 
        int i = 1;
        while(listening){
        	new BrokerLinkThread(serverSocket.accept(), brokerManager, i++).start();
        }
        serverSocket.close();
	}
}