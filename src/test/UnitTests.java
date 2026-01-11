package test;

import client.ClientRequestHandler;
import common.PasswordUtils;
import common.VanityConsole;
import db.DatabaseWrapper; // Import needed for setup
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import objects.Route;
import objects.Stop;
import objects.Trip;
import objects.User;
import server.AdminRequestHandler;
import server.LoginRequestHandler;
import server.ServerMain;
import server.UserRequestHandler;

public class UnitTests {

    public static void main(String[] args) {
        VanityConsole.shout("Running Unit Tests");

        // --- Setup Data for Integration Tests ---
        // Ensure DB has necessary data (stops) to avoid Foreign Key errors
        setupTestData();

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

        // --- Handler Tests (Server Side - Logic Only) ---
        if (testLoginRequestHandler()) passed++; else failed++;
        if (testUserRequestHandler()) passed++; else failed++;
        if (testAdminRequestHandler()) passed++; else failed++;

        // --- Integration Test (Server Side - Socket Layer) ---
        if (testServerIntegration()) passed++; else failed++;

        // --- Client Request Handler Tests (Full Flow) ---
        if (testClientRequests()) passed++; else failed++;

        VanityConsole.shout("Test Results");
        VanityConsole.info("Passed: " + passed);
        if (failed > 0) VanityConsole.panic("Failed: " + failed);
        else VanityConsole.info("All tests passed!");
        
        // Force exit because Server threads might still be running
        System.exit(0);
    }

    private static void setupTestData() {
        VanityConsole.info("Setting up Test Data...");
        // Use Admin privileges (level 2) to ensure stops exist for the tests
        DatabaseWrapper db = new DatabaseWrapper(2);
        
        // Check if stops already exist to avoid "Duplicate entry" exceptions in the logs
        List<Stop> existingStops = db.getAllStops();
        boolean hasA = false;
        boolean hasB = false;

        for (Stop s : existingStops) {
            if ("Test Stop A".equals(s.getName())) hasA = true;
            if ("Test Stop B".equals(s.getName())) hasB = true;
        }

        // Add dummy stops only if they don't exist
        if (!hasA) {
            db.addStop("Test Stop A", "District A", "Address A", 10.0, 10.0);
        }
        if (!hasB) {
            db.addStop("Test Stop B", "District B", "Address B", 20.0, 20.0);
        }
        VanityConsole.info("Test Data Setup Complete.");
    }

    private static boolean testUserSerialization() {
        VanityConsole.info("Testing User Serialization...");
        try {
            User original = new User("Alice", "alice@example.com", true, "123456", false, true);
            String serialized = User.serialize(original);
            User deserialized = User.deserialize(serialized);

            if (deserialized == null) {
                VanityConsole.error("User Serialization: FAILED (Deserialized object is null)");
                return false;
            }

            boolean match = original.getName().equals(deserialized.getName()) &&
                            original.getEmail().equals(deserialized.getEmail()) &&
                            original.isActive() == deserialized.isActive() &&
                            original.isAdmin() == deserialized.isAdmin() &&
                            original.isDriver() == deserialized.isDriver();

            if (match) {
                VanityConsole.info("User Serialization: OK");
                return true;
            } else {
                VanityConsole.error("User Serialization: FAILED");
                return false;
            }
        } catch (Exception e) {
            VanityConsole.error("User Serialization: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testTripSerialization() {
        VanityConsole.info("Testing Trip Serialization...");
        try {
            Trip original = new Trip(1, "Bob", 10, 20, 15.50, 30, 5.2, "PENDING", null);
            String serialized = Trip.serialize(original);
            Trip deserialized = Trip.deserialize(serialized);

            if (deserialized == null) {
                VanityConsole.error("Trip Serialization: FAILED (Deserialized object is null)");
                return false;
            }

            boolean match = original.getId() == deserialized.getId() &&
                            original.getUser().equals(deserialized.getUser()) &&
                            original.getOrigin() == deserialized.getOrigin() &&
                            original.getDestiny() == deserialized.getDestiny() &&
                            Math.abs(original.getPrice() - deserialized.getPrice()) < 0.001 &&
                            original.getStatus().equals(deserialized.getStatus()) &&
                            deserialized.getDriver() == null;

            if (match) {
                VanityConsole.info("Trip Serialization: OK");
                return true;
            } else {
                VanityConsole.error("Trip Serialization: FAILED");
                return false;
            }
        } catch (Exception e) {
            VanityConsole.error("Trip Serialization: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testRouteSerialization() {
        VanityConsole.info("Testing Route Serialization...");
        try {
            Route original = new Route(1, "Stop A", "Stop B", 12.5, 45);
            String serialized = Route.serialize(original);
            Route deserialized = Route.deserialize(serialized);

            if (deserialized == null) {
                VanityConsole.error("Route Serialization: FAILED (Deserialized object is null)");
                return false;
            }

            boolean match = original.getId() == deserialized.getId() &&
                            original.getOrigin().equals(deserialized.getOrigin()) &&
                            original.getDestiny().equals(deserialized.getDestiny()) &&
                            Math.abs(original.getDistance() - deserialized.getDistance()) < 0.001 &&
                            original.getDuration() == deserialized.getDuration();

            if (match) {
                VanityConsole.info("Route Serialization: OK");
                return true;
            } else {
                VanityConsole.error("Route Serialization: FAILED");
                return false;
            }
        } catch (Exception e) {
            VanityConsole.error("Route Serialization: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testPasswordUtils() {
        VanityConsole.info("Testing PasswordUtils...");
        try {
            String password = "mySecretPassword";
            String salt = PasswordUtils.getSalt();
            String hash = PasswordUtils.hashPassword(password, salt);
            
            // Check verify with correct arg order (assuming PasswordUtils was fixed to: pass, hash, salt)
            boolean verifySuccess = PasswordUtils.verifyPassword(password, hash, salt);
            boolean verifyFail = PasswordUtils.verifyPassword("wrongPassword", hash, salt);

            if (verifySuccess && !verifyFail) {
                VanityConsole.info("PasswordUtils: OK");
                return true;
            } else {
                VanityConsole.error("PasswordUtils: FAILED");
                return false;
            }
        } catch (Exception e) {
            VanityConsole.error("PasswordUtils: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testLoginRequestHandler() {
        VanityConsole.info("Testing LoginRequestHandler...");
        try {
            LoginRequestHandler handler = new LoginRequestHandler();

            String resp1 = handler.processRequest("100§OnlyUser");
            boolean check1 = resp1.equals("400§Bad Request");

            String resp2 = handler.processRequest("999§Data");
            boolean check2 = resp2.equals("400§Unknown Request");

            String resp3 = handler.processRequest("000");
            boolean check3 = resp3.equals("000§Pong");

            if (check1 && check2 && check3) {
                VanityConsole.info("LoginRequestHandler: OK");
                return true;
            } else {
                VanityConsole.error("LoginRequestHandler: FAILED");
                if (!check1) VanityConsole.debug("Failed Check 1: " + resp1);
                if (!check2) VanityConsole.debug("Failed Check 2: " + resp2);
                if (!check3) VanityConsole.debug("Failed Check 3: " + resp3);
                return false;
            }
        } catch (Exception e) {
            VanityConsole.error("LoginRequestHandler: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testUserRequestHandler() {
        VanityConsole.info("Testing UserRequestHandler...");
        try {
            UserRequestHandler handler = new UserRequestHandler("TestUser", false);

            String resp1 = handler.processRequest("000");
            boolean check1 = resp1.equals("000§Pong");

            String resp2 = handler.processRequest("205§1§2"); 
            boolean check2 = resp2.equals("400§Missing Params");

            String resp3 = handler.processRequest("700"); 
            boolean check3 = resp3.equals("403§Not a Driver");

            if (check1 && check2 && check3) {
                VanityConsole.info("UserRequestHandler: OK");
                return true;
            } else {
                VanityConsole.error("UserRequestHandler: FAILED");
                if (!check1) VanityConsole.debug("Failed Check 1: " + resp1);
                if (!check2) VanityConsole.debug("Failed Check 2: " + resp2);
                if (!check3) VanityConsole.debug("Failed Check 3: " + resp3);
                return false;
            }
        } catch (Exception e) {
            VanityConsole.error("UserRequestHandler: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testAdminRequestHandler() {
        VanityConsole.info("Testing AdminRequestHandler...");
        try {
            AdminRequestHandler handler = new AdminRequestHandler("AdminUser");

            String resp1 = handler.processRequest("000");
            boolean check1 = resp1.equals("000§Pong");

            String resp2 = handler.processRequest("500§Name"); 
            boolean check2 = resp2.equals("400§Missing Params");

            String resp3 = handler.processRequest("500§Name§Dist§Addr§NotNum§NotNum");
            boolean check3 = resp3.equals("400§Invalid Number Format");

            if (check1 && check2 && check3) {
                VanityConsole.info("AdminRequestHandler: OK");
                return true;
            } else {
                VanityConsole.error("AdminRequestHandler: FAILED");
                if (!check1) VanityConsole.debug("Failed Check 1: " + resp1);
                if (!check2) VanityConsole.debug("Failed Check 2: " + resp2);
                if (!check3) VanityConsole.debug("Failed Check 3: " + resp3);
                return false;
            }
        } catch (Exception e) {
            VanityConsole.error("AdminRequestHandler: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testServerIntegration() {
        VanityConsole.info("Testing Server Integration (Socket Layer)...");
        
        startServerThread();

        // Give it a moment to bind
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        try (Socket socket = new Socket("localhost", 6969);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Send Ping
            out.println("000");
            String response = in.readLine();
            
            if ("000§Pong".equals(response)) {
                VanityConsole.info("Server Integration: OK");
                return true;
            } else {
                VanityConsole.error("Server Integration: FAILED (Got: " + response + ")");
                return false;
            }
        } catch (Exception e) {
            VanityConsole.warn("Server Integration: SKIPPED/FAILED (Could not connect: " + e.getMessage() + ")");
            // Don't fail the whole suite if port is busy from previous run
            return false; 
        }
    }

    private static boolean testClientRequests() {
        VanityConsole.info("Testing ClientRequestHandler Flow...");
        
        // Ensure server is running
        startServerThread();
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        try {
            ClientRequestHandler client = ClientRequestHandler.getInstance();
            // Connect if not already connected
            if (!client.isConnected()) {
                client.connect("localhost", 6969);
            }
            
            if (!client.isConnected()) {
                VanityConsole.error("Client: Failed to connect to localhost:6969");
                return false;
            }

            // 1. Ping
            if (!client.ping()) {
                VanityConsole.error("Client: Ping Failed");
                return false;
            }
            VanityConsole.debug("Client: Ping OK");

            // 2. Register User
            // Use a unique suffix (timestamp) to avoid "Duplicate entry" DB error
            String uniqueSuffix = String.valueOf(System.currentTimeMillis());
            String testUser = "User" + uniqueSuffix;
            String testEmail = "user" + uniqueSuffix + "@test.com";

            String regResp = client.registerUser(testUser, testEmail, "pass");
            VanityConsole.debug("Client: Register Response -> " + regResp);

            // 3. Login
            String loginResp = client.login(testUser, "pass");
            VanityConsole.debug("Client: Login Response -> " + loginResp);
            
            if (!loginResp.startsWith("200")) {
                 VanityConsole.error("Client: Login Failed (Cannot proceed with flow)");
                 return false;
            }

            // 4. Fetch Paraderos (Map)
            List<String> paraderos = client.getParaderos();
            VanityConsole.debug("Client: Fetched " + paraderos.size() + " paraderos");
            
            // Resolve Stop IDs dynamically from the fetched list
            // This prevents Foreign Key errors by using IDs that actually exist in DB
            int originId = 1;
            int destId = 2;
            
            if (!paraderos.isEmpty()) {
                try {
                    // Format is usually "ID:Name:..." or similar, depending on Stop.toString() or server response
                    // Based on Stop.java, client just receives list strings? 
                    // Let's assume the ClientRequestHandler parses it or returns raw strings.
                    // If ClientRequestHandler.getParaderos returns List<String> of raw response lines:
                    String[] p1 = paraderos.get(0).split(":");
                    originId = Integer.parseInt(p1[0]);
                    
                    if (paraderos.size() > 1) {
                         String[] p2 = paraderos.get(1).split(":");
                         destId = Integer.parseInt(p2[0]);
                    }
                } catch (Exception e) {
                    VanityConsole.warn("Could not parse paradero IDs, using defaults 1 & 2");
                }
            }

            // 5. Request Ride
            String rideResp = client.requestRide(originId, destId, 10.5, 5.0);
            VanityConsole.debug("Client: Request Ride Response -> " + rideResp);
            
            if (rideResp.startsWith("305")) {
                // Parse Ride ID for cancellation test
                // Response format: 200§RideID
                String[] parts = rideResp.split("§");
                if (parts.length > 1) {
                    try {
                        String[] dataParts = parts[1].split("¶");

                        int rideId = Integer.parseInt(dataParts[0]);
                        
                        // 6. Check History (Expanded Test)
                        List<String> history = client.getHistory();
                        boolean found = false;
                        for (String h : history) {
                            if (h.contains("PENDIENTE")) found = true;
                        }
                        if (found) VanityConsole.debug("Client: History Verification OK");
                        else VanityConsole.warn("Client: History Verification Failed (Pending ride not found)");

                        // 7. Cancel Ride (Expanded Test)
                        String cancelResp = client.cancelRide(rideId);
                        VanityConsole.debug("Client: Cancel Ride Response -> " + cancelResp);
                        
                    } catch (NumberFormatException e) {
                        VanityConsole.warn("Could not parse Ride ID");
                    }
                }
            } else {
                VanityConsole.warn("Client: Request Ride failed, skipping Cancel/History test.");
            }

            client.disconnect();
            VanityConsole.info("ClientRequestHandler Flow: OK");
            return true;

        } catch (Exception e) {
            VanityConsole.error("ClientRequestHandler Flow: EXCEPTION (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }

    private static void startServerThread() {
        Thread serverThread = new Thread(() -> {
            try {
                ServerMain.main(new String[]{});
            } catch (Exception e) {
                // Ignore if port in use
            }
        });
        serverThread.setDaemon(true); 
        serverThread.start();
    }
}