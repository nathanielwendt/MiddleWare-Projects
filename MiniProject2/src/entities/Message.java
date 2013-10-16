package entities;

public class Message {
	public int destination; //broker ID
	public int sender; //broker ID
	public Event event;
	public String debugString;
	
	//To-Do remove this constructor and associated data member after debugging is done
	public Message(String debugString){
		this.debugString = debugString;
	}
	
	public Message(int destination, int sender, Event event){
		this.destination = destination;
		this.sender = sender;
		this.event = event;
	}
}
