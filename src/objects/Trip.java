package objects;

public class Trip {
	int id;
	String user;
	int origin;
	int destiny;
	double price;
	int duration;
	double distance;
	String status; 
	String driver; 

	public Trip(int id, String user, int origin, int destiny, double price, int duration, double distance, String status, String driver) {
		this.id = id;
		this.user = user;
		this.origin = origin;
		this.destiny = destiny;
		this.price = price;
		this.duration = duration;
		this.distance = distance;
		this.status = status;
		this.driver = driver;
	}

	// Getters and Setters
	public int getId() { return id; }
	public String getUser() { return user; }
	public int getOrigin() { return origin; }
	public int getDestiny() { return destiny; }
	public double getPrice() { return price; }
	public int getDuration() { return duration; }
	public double getDistance() { return distance; }
	public String getStatus() { return status; }
	public String getDriver() { return driver; }

	// Serializes to: ID¶UsuarioID¶OrigenID¶DestinoID¶Precio¶Duracion¶Distancia¶Estado¶Conductor
	public static String serialize(Trip trip){
		return trip.getId() + "¶" + 
		       trip.getUser() + "¶" + 
		       trip.getOrigin() + "¶" + 
		       trip.getDestiny() + "¶" + 
		       trip.getPrice() + "¶" + 
		       trip.getDuration() + "¶" + 
		       trip.getDistance() + "¶" + 
		       trip.getStatus() + "¶" + 
		       trip.getDriver();
	}

	public static Trip deserialize(String data){
		try {
			String[] parts = data.split("¶");
			// Ensure we have enough parts
			if (parts.length < 9) return null;
			
			String driverVal = parts[8].equals("null") ? null : parts[8];
			
			return new Trip(
				Integer.parseInt(parts[0]), // id
				parts[1],                   // user
				Integer.parseInt(parts[2]), // origin
				Integer.parseInt(parts[3]), // destiny
				Double.parseDouble(parts[4]), // price
				Integer.parseInt(parts[5]), // duration
				Double.parseDouble(parts[6]), // distance
				parts[7],                   // status
				driverVal                   // driver
			);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}