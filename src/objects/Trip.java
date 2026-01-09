package objects;

public class Trip {
	int id;
	String user;
	int origin;
	int destiny;
	double price;
	int duration;
	double distance;
	String status; // PENDIENTE, ACEPTADO, EN_CURSO, FINALIZADO
	String driver; // Username or ID of the driver

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

	// Serializes to: ID¶UsuarioID¶OrigenID¶DestinoID¶Precio¶Estado
	// Adapting to plan: "ID¶UsuarioID¶OrigenID¶DestinoID¶Precio¶Estado"
	public static String serialize(Trip trip){
		return trip.getId() + "¶" + trip.getUser() + "¶" + trip.getOrigin() + "¶" + trip.getDestiny() + "¶" + trip.getPrice() + "¶" + trip.getStatus();
	}

	// Helper for driver view (Code 800: ID¶OrigenNombre¶DestinoNombre¶Distancia)
	// This would require resolving IDs to names, which is done in DatabaseWrapper or Logic
}