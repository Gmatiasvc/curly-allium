package server;

import db.DatabaseWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import objects.Stop;
import objects.Trip;
import objects.User;

public class AdminRequestHandler {

    boolean isTrusted;
    String username;
    DatabaseWrapper db;

    public AdminRequestHandler(String username) {
        this.isTrusted = true;
        this.username = username;
        this.db = new DatabaseWrapper(1);
    }

    private ArrayList<String> parseRequest(String request) {
        return new ArrayList<>(Arrays.asList(request.trim().split("§")));
    }

    public String processRequest(String request) {
        ArrayList<String> params = parseRequest(request);

        if (isTrusted) {
            switch (params.get(0)) {
                case "000" -> {
                    return "Pong";
                }

                case "001" -> {
                    return "001§" + params.get(1);
                }

                case "069" -> {
                    return "069§Nice";
                }

                // 2xx: User Requests
                case "200" -> {
                    User user = db.getUserData(username);
                    if (user == null) {
                        return "403§Forbidden";
                    }
                    return "300§" + User.serialize(user);
                }

                case "202" -> {
                    String targetUsername = params.get(1);
                    boolean sucess = db.addFriend(username, targetUsername);
                    if (!sucess) {
                        return "302§User Not Found";
                    }
                    return "302§Friend Added";
                }

                case "203" -> {
                    String targetUsername = params.get(1);
                    boolean sucess = db.removeFriend(username, targetUsername);
                    if (!sucess) {
                        return "302§User Not Found";
                    }
                    return "302§Friend Added";
                }

                case "205" -> {
                    Trip trip = Trip.deserialize(params.get(1));
                    if (trip == null) {
                        return "305§Invalid Trip Data";
                    }
                    if (trip.getUser() == null || !trip.getUser().equals(username)) {
                        return "305§Unauthorized Trip Data";
                    }
                    boolean sucess = db.registerTrip(trip.getUser(), trip.getOrigin(), trip.getDestiny(), trip.getPrice(), trip.getDuration(), trip.getDistance());

                    if (!sucess) {
                        return "305§Trip Registration Failed";
                    }
                    return "305§Trip Registered Successfully";

                }

                case "206" -> {
                    String newPasswrd = params.get(1);
                    boolean sucess = db.changePassword(username, newPasswrd);
                    if (!sucess) {
                        return "306§Password Update Failed";
                    }
                    return "306§Password Updated Successfully";
                }

                case "207" -> {
                    User updatedUser = User.deserialize(params.get(1));
                    if (updatedUser == null) {
                        return "307§Invalid User Data";
                    }
                    boolean sucess = db.updateUserProfile(username, updatedUser.getName(), updatedUser.getEmail());
                    if (!sucess) {
                        return "307§User Data Update Failed";
                    }
                    username = updatedUser.getName();
                    return "307§User Data Updated Successfully";
                }

                case "208" -> {
                    isTrusted = false;
                    return "308§Untrusted connection";
                }

                case "500" -> {
                    Stop stop = Stop.deserialize(params.get(1));
                    if (stop == null) {
                        return "600§Invalid Stop Data";
                    }
                    boolean sucess = db.addStop(stop.getName(), stop.getLocation(), stop.getDistict(), stop.getLatitude(), stop.getLongitude());

                    return sucess ? "600§Stop Added Successfully" : "600§Stop Addition Failed";
                }

                case "501" -> {
                    String stopName = params.get(1);
                    boolean sucess = db.removeStop(Integer.parseInt(stopName));

                    return sucess ? "601§Stop Removed Successfully" : "601§Stop Removal Failed";
                }




                default -> {
                    return "400§Bad Request";
                }
            }
        }

        else {
            if (params.get(0).equals("101")) {
                String attemptedUsername = params.get(1);
                String attemptedPassword = params.get(2);
                boolean authSuccess = db.loginAdmin(attemptedUsername, attemptedPassword);
                if (authSuccess) {
                    this.username = attemptedUsername;
                    this.isTrusted = true;
                    return "200§Authentication Successful";
                } else {
                    return "401§Authentication Failed";
                }
            }
        }
        return "403§Forbidden";

    }
}
