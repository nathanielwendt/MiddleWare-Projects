package database;
import java.util.ArrayList;
import java.util.Date;

import events.SaleItem;

/**
 * In case we want to scale this application (and need 
 * nice speed), we should change the way thread-safety
 * is dealt with. Currently, the approach followed is
 * very inefficient and very primitive.
 * 
 * The search result method can also be further optimized by
 * using hierarchical hashmaps and also fixing the unnecessary 
 * for-loops.
 *
 */
public class InterestsDatabase extends ItemDatabase{

	public InterestsDatabase(){
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
				//get all nodes inside this node after comparison
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
				}else if(node.getData().getModifierString().trim().equals(SaleItem.MODIFIER_STRING_IGNORE)){
					//get all nodes inside this node after comparison
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
				}else if(getYearFromTimeStamp(node.getData().getTimeStamp()) == getYearFromTimeStamp(SaleItem.TIME_STAMP_IGNORE)){
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

}
