package entities;

public class Bid {
	public int itemId;
	public double currentPrice;
	
	public Bid(int itemId, double currentPrice){
		this.itemId = itemId;
		this.currentPrice = currentPrice;
	}
}
