//@file SaleItem.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ SaleItem representing an item with a value, name, date, and description

package events;
import includes.EventType;
import includes.UUIDGenerator;

import java.util.Calendar;

import setup.Init;

import com.google.gson.Gson;

import entities.Event;

//base string - Level 1
//modifier string - Level 2
//cost range - Level 3
//timestamp give ranges - Level 4

/**
 * This class can be used to 
 * represent both available item/interest
 * and the type is decided based on the
 * isInterest boolean of the class. If it is 
 * true then it is an interest.
 *
 */
public class SaleItem extends Event{
	public static final String MODIFIER_STRING_IGNORE = "$";
	public static final long TIME_STAMP_IGNORE = -1;
	public static final double COST_LOWER_BOUND_IGNORE = Double.MIN_VALUE;
	public static final double COST_UPPER_BOUND_IGNORE = Double.MAX_VALUE; 
	
	private final String baseString;
	private final String modifierString;
	private final long timeStamp;
	private final double costLowerBound;
	private final double costUpperBound;
	private final String uuid;
	private final String userUUID;
	private boolean isInterest; //if this is false, it means its an available item
	
	public static void main(String[] args){
	}
	
	public SaleItem(String baseString,String modifierString,long timeStamp,double costLowerBound, double costUpperBound,String userUUID){
		this.baseString = baseString.toLowerCase();
		this.modifierString = modifierString.toLowerCase();
		this.timeStamp = timeStamp;
		this.costLowerBound = costLowerBound;
		this.costUpperBound = costUpperBound;
		this.uuid = UUIDGenerator.getNextUUID();
		this.eventType = EventType.saleitem;
		this.userUUID = userUUID;
	}
	
	public String getBaseString() {
		return baseString;
	}
	
	public String getModifierString() {
		return modifierString;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public double getCostLowerBound() {
		return costLowerBound;
	}
	
	public double getCostUpperBound(){
		return costUpperBound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((baseString == null) ? 0 : baseString.hashCode());
		long temp;
		temp = Double.doubleToLongBits(costLowerBound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(costUpperBound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((modifierString == null) ? 0 : modifierString.hashCode());
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SaleItem))
			return false;
		SaleItem other = (SaleItem) obj;
		if(!uuid.equals(other.uuid)) return false;
		if (baseString == null) {
			if (other.baseString != null)
				return false;
		} else if (!baseString.equals(other.baseString))
			return false;
		if (Double.doubleToLongBits(costLowerBound) != Double
				.doubleToLongBits(other.costLowerBound))
			return false;
		if (Double.doubleToLongBits(costUpperBound) != Double
				.doubleToLongBits(other.costUpperBound))
			return false;
		if (modifierString == null) {
			if (other.modifierString != null)
				return false;
		} else if (!modifierString.equals(other.modifierString))
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		return true;
	}

	public boolean compare(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SaleItem)) return false;
		SaleItem other = (SaleItem) obj;
		if (baseString == null) {
			if (other.getBaseString() != null) return false;
		} else if (!baseString.equals(other.getBaseString().toLowerCase())){
			return false;
		}
		
		if (modifierString == null) {
			if (other.getModifierString() != null) return false;
		} else if (!modifierString.equals(other.getModifierString().toLowerCase()) && !other.getModifierString().equals(MODIFIER_STRING_IGNORE)
				&& !modifierString.equals(MODIFIER_STRING_IGNORE)){
			return false;
		}
		// segregated only by year!
		Calendar myDate = Calendar.getInstance();
		myDate.setTimeInMillis(timeStamp);
		Calendar otherDate = Calendar.getInstance();
		otherDate.setTimeInMillis(other.getTimeStamp());
		if (myDate.get(Calendar.YEAR) != otherDate.get(Calendar.YEAR) && other.getTimeStamp() != SaleItem.TIME_STAMP_IGNORE
				&& timeStamp != SaleItem.TIME_STAMP_IGNORE) return false;
		//checking ranges
		if (Double.doubleToLongBits(costLowerBound) >= Double
				.doubleToLongBits(other.getCostUpperBound())){
			return false;
		}
		if (Double.doubleToLongBits(costUpperBound) <= Double
				.doubleToLongBits(other.getCostLowerBound())){
			return false;
		}
		
		return true;
	}

	
	
	@Override
	public String toString() {
		return "SaleItem [baseString=" + baseString + ", uuid="
				+ uuid + ", modifierString="
				+ modifierString + ", timeStamp=" + timeStamp
				+ ", costLowerBound=" + costLowerBound + ", costUpperBound="
				+ costUpperBound + "]";
	}

	public String getUuid() {
		return uuid;
	}

	public boolean isInterest() {
		return isInterest;
	}

	public void setInterest(boolean isInterest) {
		this.isInterest = isInterest;
	}
	
	public String toJson(){
		Gson gson = Init.gsonConverter;
		return gson.toJson(this);
	}
	
	public static SaleItem getObjectFromJson(String json){
		Gson gson = Init.gsonConverter;
		return gson.fromJson(json, SaleItem.class);
	}

	public String getUserUUID() {
		return userUUID;
	}

}
