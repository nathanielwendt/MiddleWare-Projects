import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import events.SaleItem;


public class ItemDatabase {

	protected static ItemDatabase database = null;
	protected Tree<SaleItem> dataTree;

	public static ItemDatabase getInstance(){
		if(database == null){
			database = new ItemDatabase();
		}
		return database;
	}

	public ItemDatabase(){
		this.dataTree = new Tree<SaleItem>(new SaleItem("root","level",new Date().getTime(),0,10));
	}
	
	/**
	 * Returns a list of elements for the given
	 * filtering scheme.
	 * @param item
	 */
	public ArrayList<SaleItem> getSearchResult(SaleItem item){
		ArrayList<SaleItem> returnList = new ArrayList<SaleItem>();

		String baseString = item.getBaseString();
		String modifierString = item.getModifierString();
		int year = getYearFromTimeStamp(item.getTimeStamp());

		//base string level
		ArrayList<Node<SaleItem>> baseStringLevelChildren = (ArrayList<Node<SaleItem>>) dataTree.getRoot().getChildren();
		Node<SaleItem> baseStringNode = null;
		for(Node<SaleItem> node : baseStringLevelChildren){
			if(node.getData().getBaseString().trim().equals(baseString.trim())){
				baseStringNode = node;
				if(node.getData().compare(item)){
					returnList.add(node.getData());
				}
			}
		}
		if(baseStringNode == null){
			return returnList;
		}

		ArrayList<Node<SaleItem>> modifierStringLevelChildren = (ArrayList<Node<SaleItem>>) baseStringNode.getChildren();
		Node<SaleItem> modifierStringNode = null;
		if(modifierString.equals(SaleItem.MODIFIER_STRING_IGNORE)){
			for(Node<SaleItem> node : modifierStringLevelChildren){
				//get all nodes inside this node after comparision
				for(Node<SaleItem> innerNode : node.getChildren()){
					for(Node<SaleItem> innerNodeTwo : innerNode.getChildren()){
						if(innerNodeTwo.getData().compare(item)){
							returnList.add(innerNodeTwo.getData());
						}
					}
					if(innerNode.getData().compare(item)){
						returnList.add(innerNode.getData());
					}
				}
				if(node.getData().compare(item)){
					returnList.add(node.getData());
				}
			}
			return returnList;
		}else{
			for(Node<SaleItem> node : modifierStringLevelChildren){
				if(node.getData().getModifierString().trim().equals(modifierString.trim())){
					modifierStringNode = node;
					if(node.getData().compare(item)){
						returnList.add(node.getData());
					}
				}
			}
			if(modifierStringNode == null){
				return returnList;
			}
		}

		ArrayList<Node<SaleItem>> yearStringLevelChildren = (ArrayList<Node<SaleItem>>) modifierStringNode.getChildren();
		Node<SaleItem> yearStringNode = null;
		if(item.getTimeStamp() == SaleItem.TIME_STAMP_IGNORE){
			for(Node<SaleItem> node : yearStringLevelChildren){
				//get all nodes inside this node after comparision
				for(Node<SaleItem> innerNode : node.getChildren()){
					if(innerNode.getData().compare(item)){
						returnList.add(innerNode.getData());
					}
				}
				if(node.getData().compare(item)){
					returnList.add(node.getData());
				}
			}
			return returnList;
		}else{
			for(Node<SaleItem> node : yearStringLevelChildren){
				if(getYearFromTimeStamp(node.getData().getTimeStamp()) == year){
					yearStringNode = node;
					if(node.getData().compare(item)){
						returnList.add(node.getData());
					}
				}
			}
			if(yearStringNode == null){
				return returnList;
			}
		}

		// cost level
		ArrayList<Node<SaleItem>> costStringLevelChildren = (ArrayList<Node<SaleItem>>) yearStringNode.getChildren();
		for(Node<SaleItem> node : costStringLevelChildren){
			if(node.getData().compare(item)){
				returnList.add(node.getData());
			}
		}

		return returnList;
	}

	/**
	 * Deletes an element from the database if
	 * it is exactly equal to the element to be 
	 * deleted. 
	 * @param item
	 */
	public void deleteItemFromDatabase(SaleItem item){
		String baseString = item.getBaseString();
		String modifierString = item.getModifierString();
		int year = getYearFromTimeStamp(item.getTimeStamp());
		int toBeRemoved = -1;

		//base string level
		ArrayList<Node<SaleItem>> baseStringLevelChildren = (ArrayList<Node<SaleItem>>) dataTree.getRoot().getChildren();
		Node<SaleItem> baseStringNode = null;
		int size = baseStringLevelChildren.size();
		for(int i=0;i<size;i++){
			Node<SaleItem> node = baseStringLevelChildren.get(i);
			if(node.getData().getBaseString().trim().equals(baseString.trim())){
				baseStringNode = node;
				if(node.getData().equals(item)){
					toBeRemoved = i;
				}
			}
		}
		if(toBeRemoved != -1){
			baseStringLevelChildren.remove(toBeRemoved);
			toBeRemoved = -1;
		}
		if(baseStringNode == null){
			return;
		}

		//modifier string level
		ArrayList<Node<SaleItem>> modifierStringLevelChildren = (ArrayList<Node<SaleItem>>) baseStringNode.getChildren();
		Node<SaleItem> modifierStringNode = null;
		size = modifierStringLevelChildren.size();
		for(int i=0;i<size;i++){
			Node<SaleItem> node = modifierStringLevelChildren.get(i);
			if(node.getData().getBaseString().trim().equals(modifierString.trim())){
				modifierStringNode = node;
				if(node.getData().equals(item)){
					toBeRemoved = i;
				}
			}
		}

		if(toBeRemoved != -1){
			baseStringLevelChildren.remove(toBeRemoved);
			toBeRemoved = -1;
		}
		if(modifierStringNode == null){
			return;
		}

		//year level
		ArrayList<Node<SaleItem>> yearStringLevelChildren = (ArrayList<Node<SaleItem>>) modifierStringNode.getChildren();
		Node<SaleItem> yearStringNode = null;

		size = yearStringLevelChildren.size();
		for(int i=0;i<size;i++){
			Node<SaleItem> node = yearStringLevelChildren.get(i);
			if(getYearFromTimeStamp(node.getData().getTimeStamp()) == year){
				yearStringNode = node;
				if(node.getData().equals(item)){
					toBeRemoved = i;
				}
			}
		}

		if(toBeRemoved != -1){
			baseStringLevelChildren.remove(toBeRemoved);
			toBeRemoved = -1;
		}
		if(yearStringNode == null){
			return;
		}

		// cost level
		ArrayList<Node<SaleItem>> costStringLevelChildren = (ArrayList<Node<SaleItem>>) yearStringNode.getChildren();
		size = costStringLevelChildren.size();

		for(int i=0;i<size;i++){
			Node<SaleItem> node = costStringLevelChildren.get(i);
			if(node.getData().getTimeStamp() == item.getTimeStamp() &&
					node.getData().getCostUpperBound() == item.getCostUpperBound() &&
					node.getData().getCostLowerBound() == item.getCostLowerBound()&&
							node.getData().getUuid().equals(item.getUuid())){
				toBeRemoved = i;
			}
		}

		if(toBeRemoved != -1){
			costStringLevelChildren.remove(toBeRemoved);
		}
	}

	/**
	 * Adds an element to the database and makes
	 * sure we dont add duplicate element.
	 * @param item
	 */
	public void addItemToDataBase(SaleItem item){
		String baseString = item.getBaseString();
		String modifierString = item.getModifierString();
		int year = getYearFromTimeStamp(item.getTimeStamp());

		//base string level
		ArrayList<Node<SaleItem>> baseStringLevelChildren = (ArrayList<Node<SaleItem>>) dataTree.getRoot().getChildren();
		Node<SaleItem> baseStringNode = null;
		for(Node<SaleItem> node : baseStringLevelChildren){
			if(node.getData().getBaseString().trim().equals(baseString.trim())){
				baseStringNode = node;
			}
		}
		if(baseStringNode == null){
			baseStringLevelChildren.add(new Node<SaleItem>(item,dataTree.getRoot()));
			return;
		}

		//modifier string level
		ArrayList<Node<SaleItem>> modifierStringLevelChildren = (ArrayList<Node<SaleItem>>) baseStringNode.getChildren();
		Node<SaleItem> modifierStringNode = null;
		for(Node<SaleItem> node : modifierStringLevelChildren){
			if(node.getData().getModifierString().trim().equals(modifierString.trim())){
				modifierStringNode = node;
			}
		}
		if(modifierStringNode == null){
			modifierStringLevelChildren.add(new Node<SaleItem>(item,baseStringNode));
			return;
		}

		//year level
		ArrayList<Node<SaleItem>> yearStringLevelChildren = (ArrayList<Node<SaleItem>>) modifierStringNode.getChildren();
		Node<SaleItem> yearStringNode = null;
		for(Node<SaleItem> node : yearStringLevelChildren){
			if(getYearFromTimeStamp(node.getData().getTimeStamp()) == year){
				yearStringNode = node;
			}
		}
		if(yearStringNode == null){
			yearStringLevelChildren.add(new Node<SaleItem>(item,modifierStringNode));
			return;
		}

		// cost level
		ArrayList<Node<SaleItem>> costStringLevelChildren = (ArrayList<Node<SaleItem>>) yearStringNode.getChildren();
		int size = costStringLevelChildren.size();
		int toBeChecked = -1;
		for(int i=0;i<size;i++){
			Node<SaleItem> node = costStringLevelChildren.get(i);
			if(node.getData().getTimeStamp() == item.getTimeStamp() &&
					node.getData().getCostUpperBound() == item.getCostUpperBound() &&
					node.getData().getCostLowerBound() == item.getCostLowerBound()&&
							node.getData().getUuid().equals(item.getUuid())){
				toBeChecked = i;
			}
		}
		if(toBeChecked != -1){
			return;
		}
		costStringLevelChildren.add(new Node<SaleItem>(item,yearStringNode));
	}

	public int getYearFromTimeStamp(long timestamp){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		return c.get(Calendar.YEAR);
	}


	@Override
	public String toString() {
		return "" + dataTree;
	}



}
