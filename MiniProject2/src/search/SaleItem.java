package search;

import java.util.Calendar;
import java.util.Date;


public class SaleItem {
	//base string - Level 1
	//modifier string - Level 2
	//cost range - Level 3
	//timestamp give ranges - Level 4

	public static final String MODIFIER_STRING_IGNORE = "$";
	public static final long TIME_STAMP_IGNORE = -1;
	private static final double COST_LOWER_BOUND_IGNORE = Double.MIN_VALUE;
	private static final double COST_UPPER_BOUND_IGNORE = Double.MAX_VALUE; 
	
	private final String baseString;
	private final String modifierString;
	private final long timeStamp;
	private final double costLowerBound;
	private final double costUpperBound;
	
	public static void main(String[] args){
		ItemDatabase data = ItemDatabase.getInstance();
		Calendar c = Calendar.getInstance();
		SaleItem s1 = new SaleItem("Car2",SaleItem.MODIFIER_STRING_IGNORE,c.getTimeInMillis(),1000,5000);
		SaleItem s2 = new SaleItem("Car","lamborghini",c.getTimeInMillis(),1000,3000);
		c.set(Calendar.YEAR, 2012);
		SaleItem s3 = new SaleItem("Car","Mercedes",c.getTimeInMillis(),30000,50000);
		SaleItem s4 = new SaleItem("Car","lamborghini",c.getTimeInMillis(),12000,20000);
		SaleItem s5 = new SaleItem("Animal","Cat",c.getTimeInMillis(),1000,3000);
		c.set(Calendar.YEAR, 2013);
		SaleItem s6 = new SaleItem("Animal","Cat",c.getTimeInMillis(),12000,80000);
		SaleItem s7 = new SaleItem("Animal","Platypus",c.getTimeInMillis(),1000,3500);
		SaleItem s8 = new SaleItem("Object","pen",c.getTimeInMillis(),1.5,22.3);
		
		data.addItemToDataBase(s1);
		data.addItemToDataBase(s2);
		data.addItemToDataBase(s3);
		data.addItemToDataBase(s4);
		data.addItemToDataBase(s5);
		data.addItemToDataBase(s6);
		data.addItemToDataBase(s7);
		data.addItemToDataBase(s8);
		
		System.out.println(data.getSearchResult(new SaleItem("Car2","Jalepeno",SaleItem.TIME_STAMP_IGNORE,
				SaleItem.COST_LOWER_BOUND_IGNORE,90000)));
		
	}
	
	public SaleItem(String baseString,String modifierString,long timeStamp,double costLowerBound, double costUpperBound){
		this.baseString = baseString.toLowerCase();
		this.modifierString = modifierString.toLowerCase();
		this.timeStamp = timeStamp;
		this.costLowerBound = costLowerBound;
		this.costUpperBound = costUpperBound;
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
		} else if (!modifierString.equals(other.getModifierString().toLowerCase()) && !other.getModifierString().equals(MODIFIER_STRING_IGNORE)){
			return false;
		}
		
		// segregated only by year!
		Calendar myDate = Calendar.getInstance();
		myDate.setTimeInMillis(timeStamp);
		Calendar otherDate = Calendar.getInstance();
		otherDate.setTimeInMillis(other.getTimeStamp());
		if (myDate.get(Calendar.YEAR) != otherDate.get(Calendar.YEAR) && other.getTimeStamp() != SaleItem.TIME_STAMP_IGNORE) return false;
		
		//checking ranges
		if (Double.doubleToLongBits(costLowerBound) >= Double
				.doubleToLongBits(other.getCostUpperBound()))
			return false;
		if (Double.doubleToLongBits(costUpperBound) <= Double
				.doubleToLongBits(other.getCostLowerBound()))
			return false;
		
		return true;
	}

	
	
	@Override
	public String toString() {
		return "SaleItem [baseString=" + baseString + ", modifierString="
				+ modifierString + ", timeStamp=" + timeStamp
				+ ", costLowerBound=" + costLowerBound + ", costUpperBound="
				+ costUpperBound + "]";
	}
	
	

}

