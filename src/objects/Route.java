package objects;

public class Route {
	String start;
	String end;
	int distance;
	int time;
	
	public Route(String start, String end, int distance, int time) {
		this.start = start;
		this.end = end;
		this.distance = distance;
		this.time = time;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	
}
