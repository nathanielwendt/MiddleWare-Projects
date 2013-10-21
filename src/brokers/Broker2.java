package brokers;

import java.net.*;
import java.io.*;
import setup.Init;

public class Broker2 {
	
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