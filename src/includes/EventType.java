package includes;

public enum EventType {
	listItem, 
	bid, 
	bidUpdate,
	saleitem,
	interestbidupdate,
	saleNotice;
	
	public static EventType stringToEvType(String compare){
		if(compare.equals("listItem"))
			return EventType.listItem;
		else if(compare.equals("bid"))
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