package objects;

public class User {
	String name;
	String email;
	boolean isActive;
	String createdAtUT;
	boolean isAdmin;
	boolean isDriver;

	public User(String name, String email, boolean isActive, String createdAtUT, boolean isAdmin, boolean isDriver) {
		this.name = name;
		this.email = email;
		this.isActive = isActive;
		this.createdAtUT = createdAtUT;
		this.isAdmin = isAdmin;
		this.isDriver = isDriver;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getCreatedAtUT() {
		return createdAtUT;
	}

	public void setCreatedAtUT(String createdAtUT) {
		this.createdAtUT = createdAtUT;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public boolean isDriver() {
		return isDriver;
	}

	public void setDriver(boolean isDriver) {
		this.isDriver = isDriver;
	}

	// Serializes to: Nombre¶Correo¶EsAdmin¶EsConductor¶Estado
	public static String serialize(User user){
		return user.getName() + "¶" + user.getEmail() + "¶" + user.isAdmin() + "¶" + user.isDriver() + "¶" + user.isActive();
	}

	public static User deserialize(String string){
		String[] parts = string.split("¶");
		// Note: Constructor order is name, email, isActive, createdAt, isAdmin, isDriver
		// But serialization is Name, Email, Admin, Driver, Active. 
		// We map accordingly.
		return new User(
			parts[0],
			parts[1],
			Boolean.parseBoolean(parts[4]), // isActive
			"", // createdAt not in serialization for lightweight transfer
			Boolean.parseBoolean(parts[2]), // isAdmin
			Boolean.parseBoolean(parts[3])  // isDriver
		);
	}
}