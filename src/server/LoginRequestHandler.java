package server;

import db.DatabaseWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import objects.User;

public class LoginRequestHandler {

    private final DatabaseWrapper db;

    public LoginRequestHandler() {
        // Permission level 0 is sufficient for public login/register checks
        this.db = new DatabaseWrapper(0);
    }

    private ArrayList<String> parseRequest(String request) {
        return new ArrayList<>(Arrays.asList(request.trim().split("§")));
    }

    /**
     * Processes login and registration requests.
     * @param request The raw request string.
     * @return A response string starting with the response code or a serialized User object on success.
     */
    public String processRequest(String request) {
        ArrayList<String> params = parseRequest(request);
        String code = params.get(0);

        switch (code) {
            // 100: Login Genérico
            // Request: 100§UsernameOrEmail§Password
            case "100" -> {
                if (params.size() < 3) return "400§Bad Request";
                String id = params.get(1);
                String pass = params.get(2);
                User user = db.login(id, pass);
                if (user != null) {
                    // Return serialized user info so client knows role (Admin/Conductor)
                    return "200§" + User.serialize(user); 
                } else {
                    return "401§Authentication Failed";
                }
            }

            // 101: Login Admin (Legacy/Specific check if needed, mapping to generic login for simplicity)
            case "101" -> {
                if (params.size() < 3) return "400§Bad Request";
                User user = db.login(params.get(1), params.get(2));
                if (user != null && user.isAdmin()) {
                    return "200§" + User.serialize(user);
                }
                return "401§Authentication Failed or Not Admin";
            }

            // 104: Registro Pasajero
            // Request: 104§Name§Email§Password
            case "104" -> {
                if (params.size() < 4) return "400§Bad Request";
                boolean success = db.registerUser(params.get(1), params.get(2), params.get(3));
                return success ? "200§Registration Successful" : "400§Registration Failed";
            }

            // 106: Registro Conductor
            // Request: 106§Name§Email§Password
            case "106" -> {
                if (params.size() < 4) return "400§Bad Request";
                boolean success = db.registerConductor(params.get(1), params.get(2), params.get(3));
                return success ? "200§Registration Successful" : "400§Registration Failed";
            }

            // 000: Ping
            case "000" -> {
                return "000§Pong";
            }

            default -> {
                return "400§Unknown Request";
            }
        }
    }
}