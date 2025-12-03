package common;

public class Graph<T> {
	private LinkedList<LinkedList<T>> adjacencyList;
	
	public Graph() {
		adjacencyList = new LinkedList<>();
	}

	public void addVertex(T vertex) {
		adjacencyList.add(new LinkedList<>());
		adjacencyList.get(adjacencyList.size() - 1).add(vertex);
	}



}
