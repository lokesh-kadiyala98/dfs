package app.client;

import app.NodeInfo;
import app.consistent_hashing.ConsistentHash;
import app.consistent_hashing.MD5HashFunction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class ClientNode {
    private static ConsistentHash<NodeInfo> consistentHash;
    private static NodeInfo nodeInfo;
    private static Properties properties = new Properties();
    private static Socket serverSocket;

    public static void main(String[] args) {
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            String serverId = args[0];
            int portNo = Integer.parseInt(args[1]);
            int replicationFactor = Integer.parseInt(properties.getProperty("REPLICATION_FACTOR"));
            nodeInfo = new NodeInfo(serverId, properties.getProperty("IP_ADDRESS"), portNo);
            consistentHash = new ConsistentHash<>(new MD5HashFunction(), replicationFactor);

            NodeInfo serverInfo = consistentHash.get(nodeInfo);
            System.out.println(serverInfo.getNodeId());
        } catch (UnknownHostException e) {
            System.err.println("Cannot get local host address.");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading file exception.");
            e.printStackTrace();
        }
    }
}
