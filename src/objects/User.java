package objects;

public class User {
	String name;
	String email;
	boolean isActive;
	String createdAtUT;
	boolean isAdmin;

	public User(String name, String email, boolean isActive, String createdAtUT, boolean isAdmin) {
		this.name = name;
		this.email = email;
		this.isActive = isActive;
		this.createdAtUT = createdAtUT;
		this.isAdmin = isAdmin;
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

	public static String serialize(User user){
		return user.getName() + "¶" + user.getEmail() + "¶" + user.isActive() + "¶" + user.getCreatedAtUT() + "¶" + user.isAdmin();
	}

	public static User deserialize(String string){
		String[] parts = string.split("¶");
		return new User(
			parts[0],
			parts[1],
			Boolean.parseBoolean(parts[2]),
			parts[3],
			Boolean.parseBoolean(parts[4])
		);
	}
	
}
