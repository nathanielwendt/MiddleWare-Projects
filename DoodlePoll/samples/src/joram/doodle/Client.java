//@authors Nathaniel Wendt, Raga Srinivasan
//@date 11/26/2013

package doodle;

import gui.ClientGUI;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import datastructures.ClosePoll;
import datastructures.Message;
import datastructures.MessageType;
import datastructures.NewPoll;
import datastructures.Poll;
import datastructures.PollInquiry;
import datastructures.TimeSlot;

//The client polls on it's corresponding queue (stipulated by username) for messages from the Admin
//If a message is sent to the admin, it is dealt with and updates the GUI for the user.
//The client also can create polls, close polls, and receive poll requests.
public class Client {
	HashMap<String,Poll> polls = new HashMap<String,Poll>();
	public ArrayList<NewPoll> createPollMessages = new ArrayList<NewPoll>();
	public ArrayList<PollInquiry> respondToPollMessages = new ArrayList<PollInquiry>();
	public ArrayList<ClosePoll> closePollMessages = new ArrayList<ClosePoll>();

	String name;
	QueueSender requests;
	QueueReceiver updates;
	QueueSession session;
	private ClientGUI guiInstance;
	static Context ictx = null;


	public static void main(final String[] args) throws Exception {
		final Client client = new Client(args[0]);
		new Thread(new Runnable(){
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ClientGUI frame = new ClientGUI(args[0],client);
							frame.setVisible(true);
							client.guiInstance = frame;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}).start();

		javax.jms.Message msg;
		while(true){

			if(client.createPollMessages.size() > 0){
				client.createPoll(client.createPollMessages.get(0), client.createPollMessages.get(0).getRecipients());
				client.createPollMessages.remove(0);
			}

			if(client.respondToPollMessages.size() > 0){
				client.respondToPoll(client.respondToPollMessages.get(0));
				client.respondToPollMessages.remove(0);
			}

			if(client.closePollMessages.size() > 0){
				client.closePoll(client.closePollMessages.get(0));
				client.closePollMessages.remove(0);
			}
			
			msg = client.updates.receiveNoWait();
			if(msg != null){
				datastructures.Message message = (Message) ((ObjectMessage) msg).getObject();
				client.handleMessage(message);
				System.out.println(message.getSender());		
			}
		}

	}


	//Establishes connetion with reqs queue and special queue for messages sent to this client
	public Client(String name) throws Exception {
		this.name = name;
		Context ictx = new InitialContext();
		Queue reqsQ = (Queue) ictx.lookup("reqs");
		Queue updatesQ = (Queue) ictx.lookup(name);
		QueueConnectionFactory qcf = (QueueConnectionFactory) ictx.lookup("qcf");
		ictx.close();

		QueueConnection cnx = qcf.createQueueConnection();
		this.session = cnx.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		this.requests = session.createSender(reqsQ);
		this.updates = session.createReceiver(updatesQ);
		cnx.start();
	}


	//@GUI calls this method to create a poll
	//@param poll - poll to create
	//@param recipients - list of usernames to send the poll to
	public void createPoll(NewPoll newPoll, ArrayList<String> recipients) throws Exception {
		this.polls.put(newPoll.getPoll().getUUID(),newPoll.getPoll()); //might want to add this outside of the this function instead, added for safety
		ObjectMessage obj = this.session.createObjectMessage();
		obj.setObject(newPoll);
		this.requests.send(obj);
	}

	//@GUI calls this method to close a poll that it has in its active list and that it owns
	//@param poll - poll to close
	//@param finalSlot - timeslot that the creator of the poll has chosen
	public void closePoll(ClosePoll poll) throws Exception {
		ObjectMessage obj = this.session.createObjectMessage();
		obj.setObject(poll);
		this.requests.send(obj);
	}

	//@GUI calls this method to respond to a poll that is its active list
	//@param poll - poll to respond to
	public void respondToPoll(PollInquiry poll) throws Exception {
		ObjectMessage obj = this.session.createObjectMessage();
		obj.setObject(poll);
		this.requests.send(obj);
	}


	//@Handles messages incoming on the unique queue for this client
	//@param message - message to handle
	public void handleMessage(datastructures.Message message) throws Exception{
		System.out.println("Got a message from the admin!");
		//GUI should show alert box that the poll is closed
		if(message.getMessageType() == MessageType.CLOSEPOLL){
			System.out.println("Recieved Close Poll Message From admin");
			ClosePoll closePoll = (ClosePoll) message;
			this.polls.remove(closePoll.getUUID());
			guiInstance.closePoll(closePoll);
		} 
		else if(message.getMessageType() == MessageType.NEWPOLL){
			System.out.println("Recieved new Poll Message From admin");
			NewPoll poll = (NewPoll) message;
			guiInstance.showNewPollFromSomeone(poll);
		}
		//Should only get these messages if the client is the poll owner.  GUI should aggregate
		//Poll timeslot values here and show to user
		else if(message.getMessageType() == MessageType.POLLINQUIRY){
			System.out.println("Recieved Poll Inquiry Message From admin");
			PollInquiry pollInq = (PollInquiry) message;
			Poll thisPoll = pollInq.getPoll();
			this.polls.put(thisPoll.getUUID(),thisPoll);
			guiInstance.updatePollsAccordingly(thisPoll.getUUID(), pollInq);
		}
	}



}
