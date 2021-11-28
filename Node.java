public class Node {

	private Node prev;
	private int id;
	private int weight;
	
	public Node(int id){
		this.id = id;
		prev = null;
		weight = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public void setPrev(Node n) {
		prev = n;
	}
	
	public Node getPrev() {
		return prev;
	}
	
	public int getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}
}