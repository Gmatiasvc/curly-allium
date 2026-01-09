package server;

import common.VanityConsole;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import objects.User;

public class ServerMain {

    private static final int PORT = 6969;

    public static void main(String[] args) {
        VanityConsole.shout("Curly Allium Server");
        VanityConsole.info("Starting server on port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                VanityConsole.info("New client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            VanityConsole.panic("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        
        // The current handler processing requests for this client
        // Can be LoginRequestHandler, UserRequestHandler, or AdminRequestHandler
        private Object currentRequestHandler; 

        public ClientHandler(Socket socket) {
            this.socket = socket;
            // Initially, every connection starts with the Login handler
            this.currentRequestHandler = new LoginRequestHandler();
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    VanityConsole.debug("RECEIVED: " + inputLine);
                    String response = processWithCurrentHandler(inputLine);
                    
                    // Check for state transition (Login Success)
                    checkForHandlerSwitch(response);

                    VanityConsole.debug("SENDING: " + response);
                    out.println(response);
                }
            } catch (IOException e) {
                VanityConsole.warn("Client disconnected: " + socket.getInetAddress());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String processWithCurrentHandler(String request) {
            if (currentRequestHandler instanceof LoginRequestHandler) {
                return ((LoginRequestHandler) currentRequestHandler).processRequest(request);
            } else if (currentRequestHandler instanceof UserRequestHandler) {
                return ((UserRequestHandler) currentRequestHandler).processRequest(request);
            } else if (currentRequestHandler instanceof AdminRequestHandler) {
                return ((AdminRequestHandler) currentRequestHandler).processRequest(request);
            }
            return "500§Internal Handler Error";
        }

        /**
         * Intercepts the response to switch handlers if authentication was successful.
         */
        private void checkForHandlerSwitch(String response) {
            // Only switch if we are currently in Login mode
            if (currentRequestHandler instanceof LoginRequestHandler) {
                // Check for success codes (200) from LoginRequestHandler
                if (response.startsWith("200§")) {
                    try {
                        // Response format: 200§SerializedUser
                        String serializedUser = response.substring(4);
                        User user = User.deserialize(serializedUser);

                        if (user != null) {
                            if (user.isAdmin()) {
                                VanityConsole.info("Switching to AdminRequestHandler for " + user.getName());
                                this.currentRequestHandler = new AdminRequestHandler(user.getName());
                            } else {
                                VanityConsole.info("Switching to UserRequestHandler for " + user.getName() + " (Driver: " + user.isDriver() + ")");
                                this.currentRequestHandler = new UserRequestHandler(user.getName(), user.isDriver());
                            }
                            // The LoginRequestHandler instance is now dereferenced and eligible for GC
                        }
                    } catch (Exception e) {
                        VanityConsole.error("Error switching handlers: " + e.getMessage());
                        // Maintain login handler if parsing fails, client will likely error out anyway
                    }
                }
            }
            // Logic for Logout could be added here (e.g. switching back to LoginRequestHandler)
        }
    }
}