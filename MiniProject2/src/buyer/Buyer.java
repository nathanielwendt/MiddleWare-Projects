package buyer;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
 
public class Buyer {
	public LinkedBlockingQueue<Message> incoming = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> outgoing = new LinkedBlockingQueue<Message>();
	
    public static void main(String[] args) throws IOException {
 
    	Buyer buyer = new Buyer();
    	new BuyerIOThread(buyer.incoming, buyer.outgoing).start();
		buyer.SendNotice();
        while(true){
			try{
				Thread.sleep(3000);
				buyer.PrintNotices();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
        }
    }
    
    public void PrintNotices(){
    	Message nextMsg;
    	nextMsg = (Message) this.incoming.poll();
		if(nextMsg != null){
			System.out.println(nextMsg.debugString);		
		}
    	
    }
    
    public void SendNotice(){
    	Message nextMsg = new Message("hey I'm a buyer wanting to tell the brokers something");
    	this.outgoing.add(nextMsg);
    }

}