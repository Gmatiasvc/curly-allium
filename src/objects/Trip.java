package objects;

public class Trip {
	String user;
	int origin;
	int destiny;
	double price;
	int duration;
	double distance;
	
	public Trip(String user, int origin, int destiny, double price, int duration, double distance) {
		this.user = user;
		this.origin = origin;
		this.destiny = destiny;
		this.price = price;
		this.duration = duration;
		this.distance = distance;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getOrigin() {
		return origin;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}

	public int getDestiny() {
		return destiny;
	}

	public void setDestiny(int destiny) {
		this.destiny = destiny;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public static String serialize(Trip trip){
		return trip.getUser() + "¶" + trip.getOrigin() + "¶" + trip.getDestiny() + "¶" + trip.getPrice() + "¶" + trip.getDuration() + "¶" + trip.getDistance();
	}

	public static Trip deserialize(String data){
		String[] parts = data.split("¶");
		return new Trip(
			parts[0],
			Integer.parseInt(parts[1]),
			Integer.parseInt(parts[2]),
			Double.parseDouble(parts[3]),
			Integer.parseInt(parts[4]),
			Double.parseDouble(parts[5])
		);
	}
}

