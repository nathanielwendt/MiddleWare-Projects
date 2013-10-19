package includes;

public enum EventType {
	listItem, 
	bid, 
	bidUpdate, 
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
		else
			return null;
	}
}
