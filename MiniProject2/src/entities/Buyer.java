package entities;
import java.io.*;
import java.net.*;
 
public class Buyer {
    public static void main(String[] args) throws IOException {
 
        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        String host = "Nathaniel-PC";
 
        try {
            kkSocket = new Socket(host, 4000);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: masterbroker");
            System.exit(1);
        }
 
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer = "";
        String fromUser;
 
        //identify self
        out.println("AttemptConnect::Buyer");
        while(true){
        	if(in.ready()){
        		fromServer = in.readLine();
            	System.out.println("waiting for input from server");
            	System.out.println("from Server: " + fromServer);
        	}
        	if(fromServer.equals("forwarding"))
        		break;
        	if(stdIn.ready()){
        		fromUser = stdIn.readLine();
        		out.println(fromUser);
        	}
        }
        //fell out of loop because of forwarding
        fromServer = in.readLine();
        int nextPort = Integer.parseInt(fromServer);
		System.out.println("node destination is: " + nextPort);
        kkSocket.close();
        out.close();
        in.close();

        try {
            kkSocket = new Socket(host, nextPort);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: masterbroker");
            System.exit(1);
        }
        out.println("AttemptConnect::Buyer");
        while(!fromServer.equals("forwarding")){
        	if(in.ready()){
        		fromServer = in.readLine();
            	System.out.println("waiting for input from server");
            	System.out.println("from Server: " + fromServer);
        	}
        	if(stdIn.ready()){
        		fromUser = stdIn.readLine();
        		out.println(fromUser);
        	}
        }
        
       
        
        //To-Do put this in an exception from readLine
        //out.close();
        //in.close();
        //stdIn.close();
        //kkSocket.close();
    }
}