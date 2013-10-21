package seller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
import setup.Init;

public class SellerIOThread extends Thread {
	private Socket socket = null;
	private PrintWriter outstream = null;
	private BufferedReader instream = null;
	private String networkName = Init.NETWORKNAME;
	private int hostPort = Init.MASTERPORT;
	private LinkedBlockingQueue<Message> incoming = null;
	private LinkedBlockingQueue<Message> outgoing = null;


	public SellerIOThread(LinkedBlockingQueue<Message> incoming, LinkedBlockingQueue<Message> outgoing){
		this.incoming = incoming;
		this.outgoing = outgoing;
	}

	public void run() {   
		int nextPort = this.hostPort;		
		//will settle on loopOnBroker once connection is appropriate
		while(true){
			connectToBroker(nextPort);
			nextPort = loopOnBroker();
			closeOldConnection();
		}
	}

	public void connectToBroker(int nextPort){
		try {
			this.socket = new Socket(this.networkName, nextPort);
			this.outstream = new PrintWriter(this.socket.getOutputStream(), true);
			this.instream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + this.hostPort);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: Broker");
			System.exit(1);
		}
	}

	//returns the next port if loopOnBroker is closed and forwarded
	public int loopOnBroker(){
		String fromServer = "";
		int nextPort;
		//protocol to identify buyer to the server
		this.outstream.println("AttemptConnect::Seller");
		while(true) {
			try {
				if(this.instream.ready()){
					fromServer = this.instream.readLine();
					if(fromServer.equals("forwarding")){
						if(Init.VERBOSE) {
							System.out.println("Broker server full, so request forworded to another broker server.");
							fromServer = "";
						}
						break;
					}else if(fromServer.equals("Connection Established")) {
						if(Init.VERBOSE){
							System.out.println("Established persistent connection with a broker server.");
							fromServer = "";
						}
					}else{
						System.out.println("Unidentified message from server -> " + fromServer);
					}
				}
				//Send all the messages that need to be sent from the outgoing queue
				Message nextMsg = (Message) outgoing.poll();
				if(nextMsg != null){
					outstream.println(nextMsg.toJson());		
				}


				if(Init.ENABLE_THREAD_SLEEP){
					try{
						Thread.sleep(Init.THREAD_SLEEP_INTERVAL); 
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}

			} catch (IOException e){
				e.printStackTrace();
			}
		}
		try {
			fromServer = this.instream.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
		nextPort = Integer.parseInt(fromServer);
		if(Init.VERBOSE){
			System.out.println("The next proposed broker server is on port " + nextPort);
		}
		return nextPort;
	}


	/** Garbage collect connections which are not of use anymore */
	public void closeOldConnection(){
		try {
			this.socket.close();
			this.outstream.close();
			this.instream.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
