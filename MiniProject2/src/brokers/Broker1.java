package brokers;

import java.net.*;
import java.io.*;
import setup.Init;

public class Broker1 {
	
	public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
        
        BrokerManager brokerManager = new BrokerManager(0);
 
        try {
            serverSocket = new ServerSocket(Init.BROKERINDEX[0]);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + Init.MASTERPORT);
            System.exit(-1);
        }
 
        int i = 1;
        while(listening){
        	new BrokerLinkThread(serverSocket.accept(), brokerManager, i++).start();
        }
        serverSocket.close();
	}
}
