//@authors Nathaniel Wendt, Raga Srinivasan
//@date 11/26/2013

package datastructures;

//Message type is held within the Message class so
//entities can cast the messages to the appropriate sub classes
public enum MessageType {
	NEWPOLL,POLLINQUIRY,CLOSEPOLL
}