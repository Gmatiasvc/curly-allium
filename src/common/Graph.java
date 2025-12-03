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

	public void addEdge(T from, T to) {
		for (int i = 0; i < adjacencyList.size(); i++) {
			LinkedList<T> list = adjacencyList.get(i);
			if (list.get(0).equals(from)) {
				list.add(to);
				return;
			}
		}
	}

	public boolean containsVertex(T vertex) {
		for (int i = 0; i < adjacencyList.size(); i++) {
			if (adjacencyList.get(i).get(0).equals(vertex)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsEdge(T from, T to) {
		for (int i = 0; i < adjacencyList.size(); i++) {
			LinkedList<T> list = adjacencyList.get(i);
			if (list.get(0).equals(from)) {
				for (int j = 1; j < list.size(); j++) {
					if (list.get(j).equals(to)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean removeVertex(T vertex) {
		for (int i = 0; i < adjacencyList.size(); i++) {
			if (adjacencyList.get(i).get(0).equals(vertex)) {
				adjacencyList.remove(adjacencyList.get(i));
				return true;
			}
		}
		return false;
	}

	public boolean removeEdge(T from, T to) {
		for (int i = 0; i < adjacencyList.size(); i++) {
			LinkedList<T> list = adjacencyList.get(i);
			if (list.get(0).equals(from)) {
				return list.remove(to);
			}
		}
		return false;
	}
}
