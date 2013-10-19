package entities;

public class SubBlock {
	public boolean[] membership = new boolean[4];
	public String criteria; //this may need to be an object
	
	public SubBlock(){
		this.membership[0] = false;
		this.membership[1] = false;
		this.membership[2] = false;
		this.membership[3] = false;
		this.criteria = "";
	}
	
	public SubBlock(String newCriteria){
		this.membership[0] = false;
		this.membership[1] = false;
		this.membership[2] = false;
		this.membership[3] = false;
		this.criteria = newCriteria;
	}
	
	public void setMember(int pos, boolean value){
		if(pos >= 0 && pos < 4)
			this.membership[pos] = value;
	}
}
