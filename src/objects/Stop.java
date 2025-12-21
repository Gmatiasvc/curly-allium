package objects;

public class Stop {

    String name;
    String location;
    String distict;
    double latitude;
    double longitude;

    public Stop(String name, String location, String distict, double latitude, double longitude) {
        this.name = name;
        this.location = location;
        this.distict = distict;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDistict() {
        return distict;
    }

    public void setDistict(String distict) {
        this.distict = distict;
    }

    public static String serialize(Stop stop) {
        return stop.getName() + "¶" + stop.getLocation() + "¶" + stop.getDistict() + "¶" + stop.getLatitude() + "¶" + stop.getLongitude();
    }

    public static Stop deserialize(String data) {
        String[] parts = data.split("¶");
        String name = parts[0];
        String location = parts[1];
        String distict = parts[2];
        double latitude = Double.parseDouble(parts[3]);
        double longitude = Double.parseDouble(parts[4]);
        return new Stop(name, location, distict, latitude, longitude);
    }

}
