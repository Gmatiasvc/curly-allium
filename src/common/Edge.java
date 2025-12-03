package common;

public class Edge<T> {

    private T target;
    private int weight;

    public Edge(T target, int weight) {
        this.target = target;
        this.weight = weight;
    }

    public T getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

    public void setTarget(T target) {
        this.target = target;
    }

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
