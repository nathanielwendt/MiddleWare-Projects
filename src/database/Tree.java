package database;
import java.util.ArrayList;

public class Tree<T> {
	private Node<T> root;

	public Tree(T rootData) {
		root = new Node<T>(rootData,null);
		root.setChildren(new ArrayList<Node<T>>());
	}

	public Node<T> getRoot(){
		return root;
	}

	@Override
	public String toString() {
		return "root= " + root;
	}
	
	
}