package app.server;

import app.consistent_hashing.ConsistentHash;
import app.consistent_hashing.MD5HashFunction;
import app.NodeInfo;
import app.consul.ConsulServiceRegister;
import app.consul.ConsulWatcher;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class ServerNode {
    private static ConsistentHash<NodeInfo> consistentHash;
    private static NodeInfo nodeInfo;
    private static Properties properties = new Properties();
    private ServerSocket serverSocket;
    private static HealthCheckServer healthCheckServer = new HealthCheckServer();

    public void start() throws IOException {
        int consulPort = Integer.parseInt(properties.getProperty("CONSUL_PORT"));
        ConsulServiceRegister consulClient = new ConsulServiceRegister(properties.getProperty("IP_ADDRESS"), consulPort);
        String serviceName = "server";
        String serviceId = "node-" + nodeInfo.getNodeId();
        String serviceAddress = nodeInfo.getIpAddress();
        int servicePort = nodeInfo.getPortNo();
        int healthCheckServerPort = servicePort + 1000;
        String healthCheckUrl = "http://" + serviceAddress + ":" + healthCheckServerPort + "/health";
        healthCheckServer.start(healthCheckServerPort);

        consulClient.registerService(serviceName, serviceId, serviceAddress, servicePort, healthCheckUrl);

        consistentHash.add(nodeInfo);
        serverSocket = new ServerSocket(servicePort);
        System.out.println("Server started on port: " + servicePort);

        ConsulWatcher discovery = new ConsulWatcher(serviceAddress, consulPort);

        discovery.watchService("server", serviceHealth -> {
            System.out.println("Service ID: " + serviceHealth.getService().getId());
            System.out.println("Service Address: " + serviceHealth.getService().getAddress());
            System.out.println("Service Port: " + serviceHealth.getService().getPort());
        });

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);

            ClientHandler clientHandler = new ClientHandler(clientSocket, consistentHash);
            new Thread(clientHandler).start();
        }
    }

    public void stop() {
        consistentHash.remove(nodeInfo);
    }

    public static void main(String[] args) {
        ServerNode server = new ServerNode();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            String serverId = args[0];
            String ipAddress = properties.getProperty("IP_ADDRESS");
            int portNo = Integer.parseInt(args[1]);
            int replicationFactor = Integer.parseInt(properties.getProperty("REPLICATION_FACTOR"));

            nodeInfo = new NodeInfo(serverId, ipAddress, portNo);
            consistentHash = new ConsistentHash<>(new MD5HashFunction(), replicationFactor);

            server.start();
        } catch (UnknownHostException e) {
            System.err.println("Cannot get local host address.");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException.");
            e.printStackTrace();
        } finally {
            healthCheckServer.stop();
            server.stop();
        }
    }
}

