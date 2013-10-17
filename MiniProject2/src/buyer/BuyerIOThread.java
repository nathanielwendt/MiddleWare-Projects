package buyer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
import setup.Init;

public class BuyerIOThread extends Thread {
	public Socket socket = null;
    public PrintWriter outstream = null;
    public BufferedReader instream = null;
    public BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    public String networkName = Init.NETWORKNAME;
    public int hostPort = Init.MASTERPORT;
    public LinkedBlockingQueue<Message> incoming = null;
    public LinkedBlockingQueue<Message> outgoing = null;
	
	
	public BuyerIOThread(LinkedBlockingQueue<Message> incoming, LinkedBlockingQueue<Message> outgoing){
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
        String fromUser;
        int nextPort;
        
        //identify self
        this.outstream.println("AttemptConnect::Buyer");
        while(true) {
        	try {
	        	if(this.instream.ready()){
	        		fromServer = this.instream.readLine();
	            	System.out.println("waiting for input from server");
	            	System.out.println("from Server: " + fromServer);
	            	Message testmsg = new Message(fromServer);
	            	this.incoming.add(testmsg);
	        	}
	        	if(fromServer.equals("forwarding"))
	        		break;

	        	//listen for messages on incoming
	        	
	        	
	        	//To-Do remove since this is for testing only
	        	if(this.stdIn.ready()){
	        		fromUser = stdIn.readLine();
	        		this.outstream.println(fromUser);
	        	}
	        	//
	        	
				try{
					Thread.sleep(2000); //pace the thread so CPU resources can be shared
				} catch (InterruptedException e){
					e.printStackTrace();
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
		System.out.println("node destination is: " + nextPort);
		return nextPort;
	}
	
	
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
