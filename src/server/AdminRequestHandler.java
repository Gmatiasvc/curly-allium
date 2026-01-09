package server;

import db.DatabaseWrapper;
import java.util.ArrayList;
import java.util.Arrays;

public class AdminRequestHandler {

    private final String username;
    private final DatabaseWrapper db;

    public AdminRequestHandler(String username) {
        this.username = username;
        // Permission level 2 for Admin
        this.db = new DatabaseWrapper(2);
    }

    private ArrayList<String> parseRequest(String request) {
        return new ArrayList<>(Arrays.asList(request.trim().split("§")));
    }

    public String processRequest(String request) {
        ArrayList<String> params = parseRequest(request);
        String code = params.get(0);

        try {
            switch (code) {
                case "000" -> { return "000§Pong"; }

                // 500: Añadir Paradero
                // Request: 500§Nombre§Distrito§Direccion§Lat§Lon
                case "500" -> {
                    if (params.size() < 6) return "400§Missing Params";
                    boolean ok = db.addStop(
                        params.get(1), params.get(2), params.get(3),
                        Double.parseDouble(params.get(4)), Double.parseDouble(params.get(5))
                    );
                    return ok ? "600§Stop Added" : "500§Failed";
                }

                // 501: Eliminar Paradero
                // Request: 501§StopID
                case "501" -> {
                    if (params.size() < 2) return "400§Missing ID";
                    boolean ok = db.removeStop(Integer.parseInt(params.get(1)));
                    return ok ? "600§Stop Removed" : "500§Failed (In Use?)";
                }

                // 502: Añadir Ruta
                // Request: 502§OriginID§DestID§Time§Distance
                case "502" -> {
                    if (params.size() < 5) return "400§Missing Params";
                    boolean ok = db.addRoute(
                        Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)),
                        Integer.parseInt(params.get(3)), Double.parseDouble(params.get(4))
                    );
                    return ok ? "600§Route Added" : "500§Failed";
                }

                // 503: Eliminar Ruta
                // (Assuming logic exists or reusing removeRoute if implemented in wrapper)
                // Wrapper in previous context had removeRoute but prompt asked for 5xx using provided Wrapper as rough guide.
                // If Wrapper provided in prompt context doesn't have it, we skip or add placeholder.
                // The prompt's Wrapper has addRoute but not explicitly removeRoute in the 'Most up-to-date' block?
                // Wait, checking prompt content... 'removeRoute' is NOT in the latest provided DatabaseWrapper text.
                // I will skip or return NotImplemented for strictness, or assume it exists in full DB.
                // I'll stick to what's available.
                case "503" -> {
                    return "501§Not Implemented in DB Wrapper";
                }

                // 504: Modificar Paradero
                // Request: 504§ID§Nombre§Lat§Lon
                case "504" -> {
                    if (params.size() < 5) return "400§Missing Params";
                    boolean ok = db.modifyStop(
                        Integer.parseInt(params.get(1)), params.get(2),
                        Double.parseDouble(params.get(3)), Double.parseDouble(params.get(4))
                    );
                    return ok ? "600§Stop Modified" : "500§Failed";
                }

                // 508: Suspender Usuario
                // Request: 508§Username
                case "508" -> {
                    if (params.size() < 2) return "400§Missing Username";
                    boolean ok = db.suspendUser(params.get(1));
                    return ok ? "600§User Suspended" : "500§Failed";
                }

                default -> { return "400§Unknown Admin Request"; }
            }
        } catch (NumberFormatException e) {
            return "400§Invalid Number Format";
        }
    }
}