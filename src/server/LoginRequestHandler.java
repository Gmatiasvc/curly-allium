package server;

import db.DatabaseWrapper;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginRequestHandler {

    DatabaseWrapper db;

    public LoginRequestHandler() {
        this.db = new DatabaseWrapper(0);
    }

    private ArrayList<String> parseRequest(String request) {
        return new ArrayList<>(Arrays.asList(request.trim().split("§")));
    }

    public boolean processRequest(String request) {
        ArrayList<String> params = parseRequest(request);
        switch (params.get(0)) {
            
            case "100" -> {
                String username = params.get(1);
                String password = params.get(2);
                return db.loginUser(username, password);
            }
            case "101" -> {
                String username = params.get(1);
                String password = params.get(2);
                return db.loginAdmin(username, password);
            }
            case "104" -> {
                String name = params.get(1);
                String username = params.get(2);
                String email = params.get(3);
                String password = params.get(4);
                return db.registerUser(name, username, email, password);
            }
            default -> {
                return false;
            }
        }
    }
}
