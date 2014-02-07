//@authors Nathaniel Wendt, Raga Srinivasan
//@date 11/26/2013

package datastructures;

import java.util.*;
import java.io.Serializable;

//Poll object holds the information about a poll and the timeslots it holds
public class Poll implements Serializable {
	protected String uuid;
	protected String name;
	protected String description;
	protected ArrayList<TimeSlot> timeSlots;
	
	public Poll(String name, String description){
		this.name = name;
		this.description = description;
		this.uuid = UUID.randomUUID().toString();
		this.timeSlots = new ArrayList<TimeSlot>();
	}
	
	//@Adds a time slot to the poll
	//@param next - timeslot to add
	public void addTimeSlot(TimeSlot next){
		this.timeSlots.add(next);
	}

	//@Marks a timeslot within a poll to a given value
	//@param index - index of slot to mark
	//@param val - value of slot to mark
	public void markTimeSlot(int index, SlotVal val){
		this.timeSlots.get(index).setVal(val);
	}
	
	//@Gets the timeslots held within the poll
	//@returns the timeslots within the poll
	public ArrayList<TimeSlot> getTimeSlots(){
		return this.timeSlots;
	}
	
	//@Gets the name of the poll
	//@returns the string value for the name of the poll
	public String getName(){
		return this.name;
	}
	
	//@Gets the description of the poll
	//@returns the string value for the description of the poll
	public String getDescription(){
		return this.description;
	}
	
	//@Gets the unique ID of the poll
	//@returns the string value for the description of the poll
	public String getUUID(){
		return this.uuid;
	}
}