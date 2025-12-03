package common;

public class Graph<T> {
    private LinkedList<Vertex<T>> vertices;

    public Graph() {
        vertices = new LinkedList<>();
    }

    public void addVertex(T value) {
        if (!containsVertex(value)) {
            vertices.add(new Vertex<>(value));
        }
    }

    public void addEdge(T from, T to, int weight) {
        Vertex<T> fromNode = getVertex(from);
        Vertex<T> toNode = getVertex(to);

        if (fromNode != null && toNode != null) {
            if (!containsEdge(from, to)) {
                fromNode.edges.add(new Edge<>(to, weight));
            }
            
            if (!from.equals(to) && !containsEdge(to, from)) {
                toNode.edges.add(new Edge<>(from, weight));
            }
        }
    }

    public boolean removeVertex(T value) {
        Vertex<T> nodeToRemove = getVertex(value);
        
        if (nodeToRemove == null) {
            return false;
        }
        vertices.remove(nodeToRemove);

        for (int i = 0; i < vertices.size(); i++) {
            Vertex<T> currentNode = vertices.get(i);
            removeEdgeFromNode(currentNode, value);
        }

        return true;
    }

    public boolean removeEdge(T from, T to) {
        Vertex<T> fromNode = getVertex(from);
        Vertex<T> toNode = getVertex(to);
        
        boolean removedFrom = false;
        boolean removedTo = false;

        if (fromNode != null) {
            removedFrom = removeEdgeFromNode(fromNode, to);
        }

        if (toNode != null && !from.equals(to)) {
            removedTo = removeEdgeFromNode(toNode, from);
        }

        return removedFrom || removedTo;
    }

    public boolean containsVertex(T value) {
        return getVertex(value) != null;
    }

    public boolean containsEdge(T from, T to) {
        Vertex<T> node = getVertex(from);
        if (node == null) return false;

        for (int i = 0; i < node.edges.size(); i++) {
            if (node.edges.get(i).getTarget().equals(to)) {
                return true;
            }
        }
        return false;
    }


    private Vertex<T> getVertex(T value) {
        for (int i = 0; i < vertices.size(); i++) {
            Vertex<T> node = vertices.get(i);
            if (node.value.equals(value)) {
                return node;
            }
        }
        return null;
    }

    private boolean removeEdgeFromNode(Vertex<T> node, T targetVal) {
        Edge<T> edgeToRemove = null;
        
        // Find the specific Edge object instance
        for (int i = 0; i < node.edges.size(); i++) {
            Edge<T> edge = node.edges.get(i);
            if (edge.getTarget().equals(targetVal)) {
                edgeToRemove = edge;
                break;
            }
        }

        if (edgeToRemove != null) {
            return node.edges.remove(edgeToRemove);
        }
        return false;
    }
    
}