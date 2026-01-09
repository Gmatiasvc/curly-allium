package objects;

public class Route {    
	// Using Strings for Origin/Destiny to match DatabaseWrapper fetching names.
    // If IDs are required strictly by protocol, DatabaseWrapper would need to fetch IDs.
    String origin;
    String destiny;
    double distance;
    int duration;
    // Optional: ID field if needed for database updates, currently strictly generic in DBWrapper
    int id; 

    public Route(String origin, String destiny, double distance, int duration) {
        this.origin = origin;
        this.destiny = destiny;
        this.distance = distance;
        this.duration = duration;
    }

    public Route(int id, String origin, String destiny, double distance, int duration) {
        this.id = id;
        this.origin = origin;
        this.destiny = destiny;
        this.distance = distance;
        this.duration = duration;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Serializes to: ID¶Origen¶Destino¶Distancia¶Tiempo
    public static String serialize(Route route) {
        return route.getId() + "¶" +
               route.getOrigin() + "¶" +
               route.getDestiny() + "¶" +
               route.getDistance() + "¶" +
               route.getDuration();
    }

    public static Route deserialize(String data) {
        try {
            String[] parts = data.split("¶");
            if (parts.length < 5) return null;
            return new Route(
                Integer.parseInt(parts[0]), // id
                parts[1],                   // origin
                parts[2],                   // destiny
                Double.parseDouble(parts[3]), // distance
                Integer.parseInt(parts[4])  // duration
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
