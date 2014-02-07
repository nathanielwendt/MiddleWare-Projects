//@authors Nathaniel Wendt, Raga Srinivasan
//@date 11/26/2013

package datastructures;

import java.io.Serializable;

//Timeslot representing an availability on a poll
public class TimeSlot implements Serializable {
	protected SlotVal val;
	protected long startTime;
	private long endTime;
	
	public TimeSlot(SlotVal val, long startTime,long endTime){
		this.setVal(val.toString());
		this.setStartTime(startTime);
		this.endTime = endTime;
	}

	//@Gets the start time for the timeslot
	//@returns the long value of start time
	public long getStartTime() {
		return startTime;
	}

	//@Sets the start time for the timeslot
	//@param time - long value to set the starting time
	public void setStartTime(long time) {
		this.startTime = time;
	}

	//@Get the slot value
	//@returns the slot value for this timeslot
	public SlotVal getVal() {
		return val;
	}

	//@Sets the timeslot value
	//@param val - Timeslot value
	public void setVal(String val) {
		if(val.toLowerCase().equals("yes")){
			this.val = SlotVal.YES;
		}else if(val.toLowerCase().equals("no")){
			this.val = SlotVal.NO;
		}else if(val.toLowerCase().equals("maybe")){
			this.val = SlotVal.MAYBE;
		}else{
			this.val = SlotVal.NA;
		}
	}
	
	public void setVal(SlotVal val){
		this.val = val;
	}

	//@Gets the end time for the timeslot
	//@returns the the endtime in long format
	public long getEndTime() {
		return endTime;
	}

	//@Sets the end time for thetimeSlot
	//@param endTime - long value to set end time
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
}