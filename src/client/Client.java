package client;

import common.VanityConsole;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Client {

    
    // Formatter for logging timestamps
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Member variables for the client's socket and streams
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread serverListenerThread;
    private boolean connected = false;

    // A queue to hold messages received from the server.
    // This allows the listener thread to put messages and another thread to take them.
    private BlockingQueue<String> receivedMessages = new LinkedBlockingQueue<>();

    /**
     * Constructor for the ClientApp.
     * It initializes the client but does not connect to the server yet.
     */
    public Client() {
        // Constructor can be empty or set default values if needed
    }

    /**
     * Establishes a connection to the server and starts the listener thread.
     *
     * @param serverAddress The IP address or hostname of the server.
     * @param serverPort The port number the server is listening on.
     * @throws IOException If an I/O error occurs when creating the socket or streams.
     */
    public void connect(String serverAddress, int serverPort) throws IOException {
		VanityConsole.info("Connecting to server at " + serverAddress + ":" + serverPort);
        try {
            // Establish a connection to the server.
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true); // Auto-flush
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
			VanityConsole.info("Successfully connected to server at " + serverAddress + ":" + serverPort);
            // Start a separate thread to continuously read messages from the server.
            // This prevents the main thread (or the thread sending messages) from blocking.
            serverListenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while (connected && (serverMessage = in.readLine()) != null) {
                        // Instead of printing, add the message to the queue
                        receivedMessages.put(serverMessage); // This will block if the queue is full, but LinkedBlockingQueue is unbounded by default
                    }
                } catch (IOException e) {
                    // This exception typically occurs when the server closes the connection or network issues.
                    if (connected) { // Only log if we were still connected
						VanityConsole.warn("Server connection closed or error: " + e.getMessage());
						connected = false; // Mark as disconnected
					} else {
						VanityConsole.error("Server listener encountered an error after disconnection: " + e.getMessage());
                    }
                } catch (InterruptedException e) {
                    // This happens if the thread is interrupted while putting a message
                    Thread.currentThread().interrupt(); // Restore interrupt status
					VanityConsole.panic("Server listener thread interrupted while waiting to add message to queue.");
                } finally {
                    // Ensure resources are closed if the listener thread exits unexpectedly.
                    close();
                }
            });
            serverListenerThread.start(); // Start the listener thread

        } catch (IOException e) {
            connected = false;
			VanityConsole.error("Client error: Could not connect to server or I/O error occurred: " + e.getMessage());
			VanityConsole.info("Make sure the server is running on " + serverAddress + ":" + serverPort);
            throw e; // Re-throw to allow the caller to handle the connection failure
        }
    }

    /**
     * Sends a message to the connected server.
     *
     * @param message The string message to send.
     */
    public void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        } else {
			VanityConsole.error("Cannot send message: Not connected to server.");
        }
    }

    /**
     * Retrieves a message from the server's incoming message queue.
     * This method will block until a message is available or the timeout expires.
     *
     * @param timeout The maximum time to wait for a message.
     * @param unit The time unit of the timeout argument.
     * @return The received message as a String, or null if the timeout expires.
     * @throws InterruptedException If the current thread is interrupted while waiting.
     */
    public String receiveMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return receivedMessages.poll(timeout, unit); // Retrieves and removes the head of this queue, waiting up to the specified wait time if necessary for an element to become available.
    }

    /**
     * Checks if the client is currently connected to the server.
     * @return true if connected, false otherwise.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Closes the client's connection to the server and cleans up resources.
     */
    public void close() {
        // Only proceed if still marked as connected to prevent redundant closing logs/actions
        if (!connected) {
            return;
        }
        connected = false; // Mark as disconnected

        try {
            if (out != null) {
                out.close();
				VanityConsole.info("PrintWriter closed.");
            }
            if (in != null) {
                in.close();
				VanityConsole.info("BufferedReader closed.");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
				VanityConsole.info("Socket closed.");
            }
        } catch (IOException e) {
			VanityConsole.error("Error closing client resources: " + e.getMessage());
        } finally {
            // Ensure the listener thread is stopped if it's still running
            if (serverListenerThread != null && serverListenerThread.isAlive()) {
                serverListenerThread.interrupt(); // Interrupt to stop blocking readLine() or BlockingQueue.put()
                try {
                    serverListenerThread.join(1000); // Wait a bit for it to terminate
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
					VanityConsole.panic("Client listener thread join interrupted.");
                }
            }
            // Clear any remaining messages in the queue
            receivedMessages.clear();
        }
    }

    /**
     * Main method to run the client application.
     * This demonstrates how to use the ClientApp class.
     */
    public static void main(String[] args) {
        Client client = new Client();
        Scanner consoleScanner = new Scanner(System.in);

        try {
            // Connect to the server using the defined address and port
            client.connect("localhost", 6969);
			VanityConsole.info("Connected to server. Type your messages. Type 'bye' to disconnect.");

            String userInput;
            while (client.isConnected()) { // Continue as long as the client is connected
                System.out.print(VanityConsole.TERMINAL);
                userInput = consoleScanner.nextLine();

                client.sendMessage(userInput); // Use the sendMessage method

                // If the user sends "bye", break the loop to initiate client shutdown
                if (userInput.equalsIgnoreCase("bye")) {
					VanityConsole.info("Disconnecting from server...");
                    break;
                }

                // Attempt to receive a message from the server
                // We'll wait for a short period to see if a response comes back immediately
                // In a real application, you might have a more sophisticated way to handle responses
                String received = client.receiveMessage(5, TimeUnit.SECONDS); // Wait up to 5 seconds for a response
                if (received != null) {
					VanityConsole.shout("Received message from server: " + received);
                } else {
					VanityConsole.warn("No immediate response from server, it might be busy or not responding.");
                }
            }
        } catch (IOException e) {
            // Connection failed, message already printed in connect method
			VanityConsole.panic("Main thread caught IOException: " + e.getMessage());
        } catch (InterruptedException e) {
			VanityConsole.panic("Main thread interrupted while waiting for messages.");
            Thread.currentThread().interrupt(); // Restore interrupt status
        } finally {
            consoleScanner.close(); // Close the scanner
            client.close(); // Ensure client resources are closed
			VanityConsole.info("Client application terminated.");
        }
    }
}