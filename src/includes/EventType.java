//@file EventType.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ Enumeration representing the type of event being passed

package includes;

public enum EventType {
	bid, 
	bidUpdate,
	saleitem,
	interestbidupdate,
	saleNotice;

	public static EventType stringToEvType(String compare){
		if(compare.equals("bid"))
			return EventType.bid;
		else if(compare.equals("bidUpdate"))
			return EventType.bidUpdate;
		else if(compare.equals("itemSold"))
			return EventType.saleNotice;
		else if(compare.equals("availableitem") || compare.equals("interest"))
			return EventType.saleitem;
		else if(compare.equals("interestbidupdate"))
			return EventType.interestbidupdate;
		else
			return null;
	}
}
