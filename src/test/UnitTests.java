package test;

import common.PasswordUtils;
import common.VanityConsole;
import objects.Route;
import objects.Trip;
import objects.User;

public class UnitTests {

    public static void main(String[] args) {
        VanityConsole.shout("Running Unit Tests");

        int passed = 0;
        int failed = 0;

        // --- User Tests ---
        if (testUserSerialization()) passed++; else failed++;
        
        // --- Trip Tests ---
        if (testTripSerialization()) passed++; else failed++;

        // --- Route Tests ---
        if (testRouteSerialization()) passed++; else failed++;

        // --- PasswordUtils Tests ---
        if (testPasswordUtils()) passed++; else failed++;

        VanityConsole.shout("Test Results");
        VanityConsole.info("Passed: " + passed);
        if (failed > 0) VanityConsole.panic("Failed: " + failed);
        else VanityConsole.info("All tests passed!");
    }

    private static boolean testUserSerialization() {
        VanityConsole.info("Testing User Serialization...");
        User original = new User("Valentina", "Valentina@example.com", true, "123456", false, true);
        String serialized = User.serialize(original);
        User deserialized = User.deserialize(serialized);

        boolean match = original.getName().equals(deserialized.getName()) &&
                        original.getEmail().equals(deserialized.getEmail()) &&
                        original.isActive() == deserialized.isActive() &&
                        original.isAdmin() == deserialized.isAdmin() &&
                        original.isDriver() == deserialized.isDriver();

        if (match) VanityConsole.info("User Serialization: OK");
        else VanityConsole.error("User Serialization: FAILED");
        return match;
    }

    private static boolean testTripSerialization() {
        VanityConsole.info("Testing Trip Serialization...");
        Trip original = new Trip(1, "Bob", 10, 20, 15.50, 30, 5.2, "PENDING", null);
        String serialized = Trip.serialize(original);
        Trip deserialized = Trip.deserialize(serialized);

        boolean match = original.getId() == deserialized.getId() &&
                        original.getUser().equals(deserialized.getUser()) &&
                        original.getOrigin() == deserialized.getOrigin() &&
                        original.getDestiny() == deserialized.getDestiny() &&
                        Math.abs(original.getPrice() - deserialized.getPrice()) < 0.001 &&
                        original.getStatus().equals(deserialized.getStatus()) &&
                        deserialized.getDriver() == null;

        if (match) VanityConsole.info("Trip Serialization: OK");
        else VanityConsole.error("Trip Serialization: FAILED");
        return match;
    }

    private static boolean testRouteSerialization() {
        VanityConsole.info("Testing Route Serialization...");
        Route original = new Route(1, "Stop A", "Stop B", 12.5, 45);
        String serialized = Route.serialize(original);
        Route deserialized = Route.deserialize(serialized);

        boolean match = original.getId() == deserialized.getId() &&
                        original.getOrigin().equals(deserialized.getOrigin()) &&
                        original.getDestiny().equals(deserialized.getDestiny()) &&
                        Math.abs(original.getDistance() - deserialized.getDistance()) < 0.001 &&
                        original.getDuration() == deserialized.getDuration();

        if (match) VanityConsole.info("Route Serialization: OK");
        else VanityConsole.error("Route Serialization: FAILED");
        return match;
    }

    private static boolean testPasswordUtils() {
        VanityConsole.info("Testing PasswordUtils...");
        String password = "mySecretPassword";
        String salt = PasswordUtils.getSalt();
        String hash = PasswordUtils.hashPassword(password, salt);
        
        boolean verifySuccess = PasswordUtils.verifyPassword(password, hash, salt);
        boolean verifyFail = PasswordUtils.verifyPassword("wrongPassword", hash, salt);

        if (verifySuccess && !verifyFail) {
            VanityConsole.info("PasswordUtils: OK");
            return true;
        } else {
            VanityConsole.error("PasswordUtils: FAILED");
            return false;
        }
    }
}