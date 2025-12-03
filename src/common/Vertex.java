package common;

public class Vertex<T> {
	T value;
	LinkedList<Edge<T>> edges;

	public Vertex(T value) {
		this.value = value;
		this.edges = new LinkedList<>();
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public LinkedList<Edge<T>> getEdges() {
		return edges;
	}

	public void setEdges(LinkedList<Edge<T>> edges) {
		this.edges = edges;
	}

	
}
