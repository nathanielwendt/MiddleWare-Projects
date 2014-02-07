//@authors Nathaniel Wendt, Raga Srinivasan
//@date 11/26/2013

package doodle;

import java.io.BufferedReader;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.objectweb.joram.client.jms.Queue;
import org.objectweb.joram.client.jms.admin.AdminModule;
import org.objectweb.joram.client.jms.admin.User;
import org.objectweb.joram.client.jms.tcp.QueueTcpConnectionFactory;
import org.objectweb.joram.client.jms.tcp.TcpConnectionFactory;
import org.objectweb.joram.client.jms.tcp.TopicTcpConnectionFactory;

import datastructures.ClosePoll;
import datastructures.Message;
import datastructures.NewPoll;
import datastructures.Poll;
import datastructures.PollInquiry;
import datastructures.ClosePoll;
import datastructures.MessageType;
import datastructures.TimeSlot;
import datastructures.SlotVal;

//The admin sets up all of the queues necessary for message passing.
//The admin then listens for requests sent by clients and routes and processes them appropriately.
public class Admin {
	public static ArrayList<String> members = new ArrayList<String>();
	public static final String MEMBERS_FILE_PATH =  "../src/joram/doodle/members.txt";
	public static HashMap<String,ArrayList<String>> pollMembership = new HashMap<String,ArrayList<String>>(); //key is poll uuid, key is arraylist of username of members
	public static HashMap<String,String> pollOwners = new HashMap<String,String>(); //key is poll uuid, value is username of owner
	public static HashMap<String,QueueSender> memberQueues = new HashMap<String,QueueSender>();
	public static QueueSession session;
	public static QueueReceiver receiver;

	public static void main(String[] args) throws Exception {

		System.out.println();
		System.out.println("Admin Running...");

		AdminModule.connect("root", "root", 60);
		ReadInputsFromFile();
		SetupQueues();

		//Setup is done
		//Now poll for requests

		javax.jms.Message msg;
		while(true){
			msg = receiver.receive();
			datastructures.Message message = (Message) ((ObjectMessage) msg).getObject();
			HandleMessage(message);
		}

		//Should disconnect when done if polling ever ends in future updates
		//AdminModule.disconnect();
		//System.out.println("Admin closed.");
	}

	//@Reads in the members list from a text file
	//@post members is populated from members.txt
	public static void ReadInputsFromFile(){
		try{
			FileInputStream fstream = new FileInputStream(MEMBERS_FILE_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String nextMember;
			while((nextMember = br.readLine()) != null){
				members.add(nextMember);
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	//@Establishes a reqs queue that all clients can connect to to send messages to the admin
	//@Establishes an update queue for each client.  This is where all messages to clients are sent
	//@Joram binding is also done here so clients can reference appropriate queues.
	public static void SetupQueues() throws Exception{

		Queue reqs = Queue.create("reqs");
		reqs.setFreeWriting();
		reqs.setFreeReading();
		User.create("anonymous", "anonymous");
		javax.jms.ConnectionFactory cf = TcpConnectionFactory.create("localhost", 16010);
		javax.jms.QueueConnectionFactory qcf = QueueTcpConnectionFactory.create("localhost", 16010);
		javax.jms.TopicConnectionFactory tcf = TopicTcpConnectionFactory.create("localhost", 16010);

		javax.naming.Context jndiCtx = new javax.naming.InitialContext();
		jndiCtx.bind("reqs", reqs);
		jndiCtx.bind("cf", cf);
		jndiCtx.bind("qcf", qcf);
		jndiCtx.bind("tcf", tcf);

		QueueConnection cnx = qcf.createQueueConnection();
		session = cnx.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		receiver = session.createReceiver(reqs);
		cnx.start();

		for(int i = 0; i < members.size(); ++i){
			System.out.println(members.get(i));
			Queue updates = Queue.create(members.get(i));
			updates.setFreeWriting();
			updates.setFreeReading();
			jndiCtx.bind(members.get(i), updates);

			QueueSender sender = session.createSender(updates);
			memberQueues.put(members.get(i),sender);
		}    
		jndiCtx.close(); //done with binding

	}

	//Handles message routing and delivery.  See if statements for routing logic
	//@param message - message to be interpreted and processed
	public static void HandleMessage(datastructures.Message message) throws Exception{

		//If the message is a new poll, the admin needs to notify all recipients with poll inquiry
		//messages and needs to log the poll membership information for lookup later.
		if(message.getMessageType() == MessageType.NEWPOLL){
			System.out.println("Recieved New Poll Message From client");
			NewPoll newPoll = (NewPoll) message;

			System.out.println("Sender is -> " + newPoll.getSender());

			ArrayList<String> recipients = newPoll.getRecipients();
			PollInquiry pollInquiry = new PollInquiry(newPoll.getPoll(),newPoll.getSender());
			pollMembership.put(newPoll.getPoll().getUUID(), recipients);
			pollOwners.put(newPoll.getPoll().getUUID(), newPoll.getSender());

			System.out.println("Sending to "  + recipients.toString());

			for(int i = 0; i < recipients.size(); i++){
				ObjectMessage obj = session.createObjectMessage();
				obj.setObject(newPoll);
				QueueSender destination = memberQueues.get(recipients.get(i));
				if(destination != null)
					destination.send(obj);
			}
		} 
		//If the message is a close poll, the admin needs to find all members attached to that
		//poll and notify them that the poll has been closed.  Then, the admin cleans up the
		//owner and membership maps so that all subsequent inquiries sent will not be found (error checking is ensured in that case)
		else if(message.getMessageType() == MessageType.CLOSEPOLL){
			System.out.println("Recieved Close Poll Message From client");
			ClosePoll closePoll = (ClosePoll) message;
			ArrayList<String> recipients = closePoll.getRecipients();
			for(int i = 0; i < recipients.size(); i++){
				ObjectMessage obj = session.createObjectMessage();
				obj.setObject(closePoll);
				QueueSender destination = memberQueues.get(recipients.get(i));
				if(destination != null)
					destination.send(obj);
			}
			//perform cleanup that all additional inquiries sent after closing the poll will not be received
			pollMembership.remove(closePoll.getUUID());
			pollOwners.remove(closePoll.getUUID());
		}
		//If the message is a poll inquiry, the admin needs to look up the poll owner,
		//and send the pollinquiry data to the poll owner for aggregation
		else if(message.getMessageType() == MessageType.POLLINQUIRY){
			System.out.println("Recieved Poll Inquiry Message From client");
			PollInquiry pollInq = (PollInquiry) message;
			ArrayList<String> recipients = pollInq.getRecipients();

			System.out.println("Sending poll inquiry to "  + recipients.toString());

			for(int i = 0; i < recipients.size(); i++){
				ObjectMessage obj = session.createObjectMessage();
				obj.setObject(pollInq);
				QueueSender destination = memberQueues.get(recipients.get(i));
				if(destination != null)
					destination.send(obj);
			}
		}
	}
}
