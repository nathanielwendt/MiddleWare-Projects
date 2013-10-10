import java.util.List;


 public class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;
        
        
        public Node(){};
        
		public T getData() {
			return data;
		}
		
		public void setData(T data) {
			this.data = data;
		}
		
		public Node<T> getParent() {
			return parent;
		}
		
		public void setParent(Node<T> parent) {
			this.parent = parent;
		}
		
		public void addChild(Node<T> child){
			this.children.add(child);
		}
		
		public void removeChild(Node<T> child){
			this.children.remove(child);
		}

		public List<Node<T>> getChildren() {
			return children;
		}

		public void setChildren(List<Node<T>> children) {
			this.children = children;
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((children == null) ? 0 : children.hashCode());
			result = prime * result + ((data == null) ? 0 : data.hashCode());
			result = prime * result
					+ ((parent == null) ? 0 : parent.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object o){
			if(o == null) return false;
			if (!(o instanceof Node)) return false;
			Node<T> other = (Node<T>)o;
			int length = this.children.size();
			for(int i=0;i<length;i++){
				if(!this.children.get(i).equals(other.getChildren().get(i))){
					return false;
				}
			}
			if(!other.getData().equals(this.data)) return false;
			return true;
		}
    }