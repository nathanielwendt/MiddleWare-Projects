//@authors Nathaniel Wendt, Raga Srinivasan
//@date 11/26/2013

package datastructures;

import java.io.Serializable;
import java.util.ArrayList;

//A poll inquiry serves as both the request for poll information and
//for the returned value with filled in poll information
public class PollInquiry extends Message implements Serializable {
	protected Poll poll;
	protected ArrayList<String> recipients;

	public PollInquiry(Poll poll, String sender){
		super(sender);
		this.mType = MessageType.POLLINQUIRY;
		this.setPoll(poll);
		this.recipients = new ArrayList<String>();
	}
	
	public void setRecipients(ArrayList<String> recipients){
		this.recipients = recipients;
	}
	
	public ArrayList<String> getRecipients(){
		return this.recipients;
	}

	//@Gets the poll held within the inquiry message
	//@returns the poll
	public Poll getPoll() {
		return poll;
	}

	//@Sets the poll held within the inquiry message
	//@param poll - poll to send message about
	public void setPoll(Poll poll) {
		this.poll = poll;
	}
}