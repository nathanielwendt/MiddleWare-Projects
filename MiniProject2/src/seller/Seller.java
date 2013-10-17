package seller;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

import entities.Message;
 
public class Seller {
	public LinkedBlockingQueue<Message> incoming = new LinkedBlockingQueue<Message>();
	public LinkedBlockingQueue<Message> outgoing = new LinkedBlockingQueue<Message>();
	
    public static void main(String[] args) throws IOException {
 
    	Seller seller = new Seller();
    	new SellerIOThread(seller.incoming, seller.outgoing).start();
        while(true){
			try{
				Thread.sleep(3000);
				seller.PrintNotices();
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

}