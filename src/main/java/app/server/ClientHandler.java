package app.server;

import app.NodeInfo;
import app.consistent_hashing.ConsistentHash;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ConsistentHash<NodeInfo> consistentHash;

    public ClientHandler(Socket socket, ConsistentHash<NodeInfo> consistentHash) {
        this.clientSocket = socket;
        this.consistentHash = consistentHash;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Process the input and generate a response
                String response = processInput(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Exception in ClientHandler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String processInput(String input) {
        // Implement your request processing logic here
        return "Echo: " + input; // Example response
    }
}

