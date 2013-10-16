package entities;
import java.util.*;

public class Item {
	static int unique_count = 0;
	public int id;
	public ArrayList<String> attributes = new ArrayList<String>();
	public String description;
	public double minimumBid;

	public Item(){
		this.id = unique_count++;
	}
	
	public Item(ArrayList<String> attributes, String description, double minimumBid){
		this.id = unique_count++;
		this.attributes = attributes;
		this.description = description;
		this.minimumBid = minimumBid;
	}
	
	public void PrintItem(){
		System.out.println("\n -------------------------");
		System.out.println("Id: " + this.id + "\n");
		System.out.println("Name: " + this.description + "\n");
		System.out.println("Current Bid: " + this.minimumBid + "\n");
		System.out.println(" -------------------------");
	}
}
