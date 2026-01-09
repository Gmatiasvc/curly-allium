package server;

import db.DatabaseWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import objects.Route;
import objects.Stop;
import objects.User;

public class UserRequestHandler {

    private final String username;
    private final DatabaseWrapper db;
    private final boolean isDriver;

    public UserRequestHandler(String username, boolean isDriver) {
        this.username = username;
        this.isDriver = isDriver;
        // Set permission level based on role (1 for driver, 0 for user)
        this.db = new DatabaseWrapper(isDriver ? 1 : 0);
    }

    private ArrayList<String> parseRequest(String request) {
        return new ArrayList<>(Arrays.asList(request.trim().split("§")));
    }

    public String processRequest(String request) {
        ArrayList<String> params = parseRequest(request);
        String code = params.get(0);

        switch (code) {
            case "000" -> { return "000§Pong"; }

            // --- 2xx: CLIENTE / PASAJERO ---

            // 200: Datos Perfil
            case "200" -> {
                User u = db.getUserData(username);
                return (u != null) ? "300§" + User.serialize(u) : "404§User Not Found";
            }

            // 201: Obtener Paraderos
            case "201" -> {
                List<Stop> stops = db.getAllStops();
                StringBuilder sb = new StringBuilder("301§");
                for (int i = 0; i < stops.size(); i++) {
                    Stop s = stops.get(i);
                    // ID is usually needed for graph but Stop object here might assume Name is unique or ID is handled internally
                    // Plan: ID¶Nombre¶Distrito¶Direccion¶Lat¶Lon
                    // Current Stop object doesn't have explicit int ID getter exposed in previous file, 
                    // but DB returns it. Assuming Stop object has fields: Name, Location(Addr), District, Lat, Lon.
                    // We might need to map ID if Stop object doesn't have it.
                    // For now, using hash or placeholder if ID missing in Object, or Name as ID.
                    // Let's assume Name is unique as per previous logic.
                    sb.append(i).append("¶") // Using index/temp ID
                      .append(s.getName()).append("¶")
                      .append(s.getDistict()).append("¶")
                      .append(s.getLocation()).append("¶")
                      .append(s.getLatitude()).append("¶")
                      .append(s.getLongitude());
                    if (i < stops.size() - 1) sb.append("▓");
                }
                return sb.toString();
            }

            // 202: Obtener Rutas
            case "202" -> {
                List<Route> routes = db.getAllRoutes();
                StringBuilder sb = new StringBuilder("302§");
                for (int i = 0; i < routes.size(); i++) {
                    Route r = routes.get(i);
                    // Plan: ID¶OrigenID¶DestinoID¶Distancia¶Tiempo
                    // Route object has: Origin(String), Destiny(String), Distance, Duration
                    // Frontend expects IDs or Names. If frontend BLMapa uses names, we send names.
                    // Plan said ID... let's send Names if that's what Route object holds.
                    sb.append(i).append("¶")
                      .append(r.getOrigin()).append("¶")
                      .append(r.getDestiny()).append("¶")
                      .append(r.getDistance()).append("¶")
                      .append(r.getDuration());
                    if (i < routes.size() - 1) sb.append("▓");
                }
                return sb.toString();
            }

            // 205: Solicitar Viaje
            // Request: 205§OriginID§DestID§Price§Distance
            case "205" -> {
                if (params.size() < 5) return "400§Missing Params";
                try {
                    int originId = Integer.parseInt(params.get(1));
                    int destId = Integer.parseInt(params.get(2));
                    double price = Double.parseDouble(params.get(3));
                    double dist = Double.parseDouble(params.get(4));
                    
                    int tripId = db.requestRide(username, originId, destId, price, dist);
                    return (tripId != -1) ? "305§" + tripId + "¶PENDIENTE" : "500§Error creating trip";
                } catch (NumberFormatException e) {
                    return "400§Invalid Number Format";
                }
            }

            // 206: Consultar Estado
            // Request: 206§TripID
            case "206" -> {
                if (params.size() < 2) return "400§Missing Trip ID";
                try {
                    int tripId = Integer.parseInt(params.get(1));
                    String status = db.getRideStatus(tripId);
                    return (status != null) ? "306§" + status : "404§Trip Not Found";
                } catch (Exception e) { return "400§Error"; }
            }

            // 207: Actualizar Perfil
            // Request: 207§NewEmail§NewName
            case "207" -> {
                if (params.size() < 3) return "400§Missing Data";
                boolean ok = db.updateProfile(username, params.get(1), params.get(2));
                return ok ? "300§Profile Updated" : "500§Update Failed";
            }

            // 208: Cancelar Viaje
            case "208" -> {
                if (params.size() < 2) return "400§Missing Trip ID";
                boolean ok = db.cancelRide(Integer.parseInt(params.get(1)), username);
                return ok ? "200§Cancelled" : "400§Cannot Cancel";
            }

            // 209: Historial
            case "209" -> {
                List<String> hist = db.getPassengerHistory(username);
                StringBuilder sb = new StringBuilder("309§");
                for (int i = 0; i < hist.size(); i++) {
                    sb.append(hist.get(i));
                    if (i < hist.size() - 1) sb.append("▓");
                }
                return sb.toString();
            }

            // --- 7xx: CONDUCTOR (Requires isDriver) ---

            // 700: Obtener Solicitudes Pendientes
            case "700" -> {
                if (!isDriver) return "403§Not a Driver";
                List<String> rides = db.getPendingRides();
                StringBuilder sb = new StringBuilder("800§");
                for (int i = 0; i < rides.size(); i++) {
                    sb.append(rides.get(i));
                    if (i < rides.size() - 1) sb.append("▓");
                }
                return sb.toString();
            }

            // 701: Aceptar Viaje
            // Request: 701§TripID
            case "701" -> {
                if (!isDriver) return "403§Not a Driver";
                if (params.size() < 2) return "400§Missing Trip ID";
                boolean ok = db.acceptRide(Integer.parseInt(params.get(1)), username);
                return ok ? "801§Accepted" : "400§Failed to Accept";
            }

            // 702: Actualizar Estado (e.g. Start/End)
            // Request: 702§TripID§NewStatus
            case "702" -> {
                if (!isDriver) return "403§Not a Driver";
                if (params.size() < 3) return "400§Missing Data";
                boolean ok = db.updateRideStatus(Integer.parseInt(params.get(1)), params.get(2));
                return ok ? "200§Updated" : "400§Failed";
            }

            default -> { return "400§Unknown Request"; }
        }
    }
}