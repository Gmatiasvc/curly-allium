package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientRequestHandler {

    private static ClientRequestHandler instance;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    // Configuration
    private String serverHost = "localhost";
    private int serverPort = 6969;
    private boolean isConnected = false;

    // Private constructor for Singleton
    private ClientRequestHandler() {}

    /**
     * Get the singleton instance.
     */
    public static synchronized ClientRequestHandler getInstance() {
        if (instance == null) {
            instance = new ClientRequestHandler();
        }
        return instance;
    }

    /**
     * Connect to the server. Call this at application startup.
     */
    public void connect(String host, int port) throws IOException {
        this.serverHost = host;
        this.serverPort = port;
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.isConnected = true;
        System.out.println("Connected to server at " + host + ":" + port);
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Generic method to send a request and receive the raw response.
     * Protocol: CODE§Param1§Param2...
     */
    private synchronized String sendRequest(String code, String... params) {
        if (!isConnected) return "500§Not Connected";

        StringBuilder sb = new StringBuilder();
        sb.append(code);
        for (String param : params) {
            sb.append("§").append(param);
        }

        System.out.println("CLIENT SEND: " + sb.toString());
        out.println(sb.toString());

        try {
            String response = in.readLine();
            System.out.println("CLIENT RECV: " + response);
            if (response == null) {
                isConnected = false;
                return "500§Server Connection Lost";
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
            return "500§IO Error";
        }
    }

    // --- 0xx: UTILIDADES ---

    public boolean ping() {
        String resp = sendRequest("000");
        return resp.startsWith("000");
    }

    // --- 1xx: AUTENTICACIÓN ---

    /**
     * 100: Login Genérico
     * @return Response string. Format: "200§Nombre¶Correo¶Admin¶Driver¶Active" or "401§Error"
     */
    public String login(String userOrEmail, String password) {
        return sendRequest("100", userOrEmail, password);
    }

    /**
     * 104: Registro Pasajero
     */
    public String registerUser(String name, String email, String password) {
        return sendRequest("104", name, email, password);
    }

    /**
     * 106: Registro Conductor
     */
    public String registerConductor(String name, String email, String password) {
        return sendRequest("106", name, email, password);
    }

    /**
     * 105: Registro Admin
     */
    public String registerAdmin(String name, String email, String password) {
        return sendRequest("105", name, email, password);
    }

    // --- 2xx: CLIENTE / PASAJERO ---

    /**
     * 200: Obtener Datos de Perfil
     * Response: "300§Nombre¶Correo¶Admin¶Driver¶Active"
     */
    public String getProfile(String username) {
        // Backend UserRequestHandler handles "200" by using the session username if it were stateful,
        // but currently ServerMain passes a username. The Client just sends the code. 
        // Note: The backend implementation relies on the Handler knowing the user. 
        // If the protocol requires sending the username in the request, we should add it.
        // Assuming the socket session is stateful on the server (Handler knows the user).
        return sendRequest("200"); 
    }

    /**
     * 201: Obtener Paraderos
     * Returns a list of serialized strings: "ID¶Nombre¶Distrito¶Direccion¶Lat¶Lon"
     */
    public List<String> getParaderos() {
        String resp = sendRequest("201");
        return parseListResponse(resp, "301");
    }

    /**
     * 202: Obtener Rutas
     * Returns a list of serialized strings: "ID¶Origen¶Destino¶Distancia¶Tiempo"
     */
    public List<String> getRutas() {
        String resp = sendRequest("202");
        return parseListResponse(resp, "302");
    }

    /**
     * 205: Solicitar Viaje
     */
    public String requestRide(int originId, int destId, double price, double distance) {
        return sendRequest("205", 
            String.valueOf(originId), 
            String.valueOf(destId), 
            String.valueOf(price), 
            String.valueOf(distance)
        );
    }

    /**
     * 206: Consultar Estado de Viaje
     */
    public String checkRideStatus(int tripId) {
        return sendRequest("206", String.valueOf(tripId));
    }

    /**
     * 207: Actualizar Perfil
     */
    public String updateProfile(String newEmail, String newName) {
        return sendRequest("207", newEmail, newName);
    }

    /**
     * 208: Cancelar Viaje
     */
    public String cancelRide(int tripId) {
        return sendRequest("208", String.valueOf(tripId));
    }

    /**
     * 209: Historial Pasajero
     * Returns list of "Origin->Dest [Status]" strings
     */
    public List<String> getHistory() {
        String resp = sendRequest("209");
        return parseListResponse(resp, "309");
    }

    // --- 5xx: ADMINISTRADOR ---

    public String addStop(String nombre, String distrito, String direccion, double lat, double lon) {
        return sendRequest("500", nombre, distrito, direccion, String.valueOf(lat), String.valueOf(lon));
    }

    public String removeStop(int stopId) {
        return sendRequest("501", String.valueOf(stopId));
    }

    public String addRoute(int originId, int destId, int time, double dist) {
        return sendRequest("502", 
            String.valueOf(originId), String.valueOf(destId), 
            String.valueOf(time), String.valueOf(dist)
        );
    }

    public String modifyStop(int id, String nombre, double lat, double lon) {
        return sendRequest("504", 
            String.valueOf(id), nombre, String.valueOf(lat), String.valueOf(lon)
        );
    }

    public String suspendUser(String username) {
        return sendRequest("508", username);
    }

    // --- 7xx: CONDUCTOR ---

    /**
     * 700: Obtener Solicitudes Pendientes
     * Returns list of "ViajeID¶OrigenNombre¶DestinoNombre¶Distancia"
     */
    public List<String> getPendingRides() {
        String resp = sendRequest("700");
        return parseListResponse(resp, "800");
    }

    /**
     * 701: Aceptar Viaje
     */
    public String acceptRide(int tripId) {
        return sendRequest("701", String.valueOf(tripId));
    }

    /**
     * 702: Actualizar Estado de Viaje
     * newStatus: "EN_CURSO", "FINALIZADO"
     */
    public String updateRideStatus(int tripId, String newStatus) {
        return sendRequest("702", String.valueOf(tripId), newStatus);
    }

    // --- HELPERS ---

    /**
     * Parses a response containing a list delimited by ▓.
     * Format: CODE§Item1▓Item2▓Item3...
     */
    private List<String> parseListResponse(String response, String expectedCode) {
        List<String> list = new ArrayList<>();
        if (response == null || !response.startsWith(expectedCode + "§")) {
            return list; // Return empty or handle error
        }

        // Remove CODE§
        String content = response.substring(response.indexOf("§") + 1);
        if (content.isEmpty()) return list;

        // Split by ▓
        String[] items = content.split("▓");
        list.addAll(Arrays.asList(items));
        return list;
    }
}