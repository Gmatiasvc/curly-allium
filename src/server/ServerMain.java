package server;

import common.VanityConsole;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // To show current time in messages
import java.util.concurrent.TimeUnit;

public class ServerMain {

    private static final int PORT = 6969; // Funi port
    // Define the size of our thread pool.

    private static final int THREAD_POOL_SIZE = 5; // Allows 5 clients to be processed concurrently

    public static void main(String[] args) {
        // Create a fixed-size thread pool.
        // This pool will manage a set number of threads to handle incoming client connections.
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // Use a try-with-resources statement to ensure the ServerSocket is closed
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            VanityConsole.info("Server started on port " + PORT);
            VanityConsole.info("Thread pool size: " + THREAD_POOL_SIZE);
            VanityConsole.shout("Waiting for client connections...");

            // The server runs indefinitely, continuously accepting new client connections
            while (true) {
                try {

                    // serverSocket.accept() blocks until a client connects.
                    // When a client connects, a new Socket object is returned, representing that specific connection.
                    Socket clientSocket = serverSocket.accept();
                    String clientAddress = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                    VanityConsole.info("Client connected from: " + clientAddress);

                    // Submit the client handling logic as a Runnable task to the thread pool.
                    // The ExecutorService will pick an available thread from its pool to execute this task.
                    executor.submit(new ClientHandler(clientSocket, clientAddress));

                } catch (IOException e) {
                    VanityConsole.error("Error accepting client connection: " + e.getMessage());
                    // In a production server, you might log this error and continue,
                    // or implement a more sophisticated error recovery mechanism.
                }
            }

        } catch (IOException e) {
            VanityConsole.panic("Server failed to start. " + e.getMessage());
            // If the server fails to start, we should exit the application.
            System.exit(1);

        } finally {
            // It's crucial to shut down the executor service when the server application is closing.
            // This prevents resource leaks and ensures all submitted tasks are completed or gracefully terminated.
            VanityConsole.shout("Shutting down server and thread pool...");
            executor.shutdown(); // Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
            try {
                // Wait a certain amount of time for existing tasks to complete.
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    // If tasks don't complete within the timeout, force shutdown all running tasks.
                    executor.shutdownNow();
                    VanityConsole.warn("Thread pool did not terminate gracefully. Forcing shutdown.");
                } else {
                    VanityConsole.info("Thread pool terminated gracefully.");
                }
            } catch (InterruptedException ex) {
                // Re-interrupt the current thread if interrupted while waiting for termination.
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                VanityConsole.error("Server shutdown interrupted: " + ex.getMessage());
            }
            VanityConsole.shout("Server has been stopped. Goodbye!");
        }
    }

    /**
     * A Runnable class to handle communication with a single client. Each
     * instance of this class will run in its own thread from the
     * ExecutorService pool.
     */
	@SuppressWarnings("FieldMayBeFinal")
    private static class ClientHandler implements Runnable {

        private Socket clientSocket;
        private String clientAddress;
		//private UserRequestHandler processor = new UserRequestHandler(); // Instance of Connection class to process requests

        public ClientHandler(Socket clientSocket, String clientAddress) {
            this.clientSocket = clientSocket;
            this.clientAddress = clientAddress;
        }

        @Override
        public void run() {

        }
    }
}