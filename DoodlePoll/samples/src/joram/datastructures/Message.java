//@authors Nathaniel Wendt, Raga Srinivasan
//@date 11/26/2013

package datastructures;

import java.io.Serializable;

//A message holds some information as to the type of message, and sender of the message
//All messages should extend Message
public class Message implements Serializable {
	protected String sender;
	protected MessageType mType;
	
	//@Constructor sets sender of message to "default sender"
	public Message(){
		this.sender = "default sender";
	}
	
	//@Constructor for defining custom message sender
	//@param sender - String username of sender
	public Message(String sender){
		this.sender = sender;
	}
	
	//@Gets the sender of the message
	//@returns the string username value of the sender
	public String getSender(){
		return this.sender;
	}
	
	//@Gets the type of message for this instance
	//@returns the messagetype corresponding to this message
	public MessageType getMessageType(){
		return this.mType;
	}
}