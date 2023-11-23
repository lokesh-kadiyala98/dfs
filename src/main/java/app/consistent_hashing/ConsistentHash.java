package app.consistent_hashing;

import app.NodeInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash<T> {
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = new TreeMap<>();
    private final HashFunction hashFunction;

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
    }

    public void add(T nodeInfo) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hash(nodeInfo.toString() + i), nodeInfo);
        }
    }

    public void remove(T nodeInfo) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(nodeInfo.toString() + i));
        }
    }

    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key.toString());
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            int replicationFactor = Integer.parseInt(properties.getProperty("REPLICATION_FACTOR"));
            ConsistentHash<NodeInfo> consistentHash = new ConsistentHash<>(new MD5HashFunction(), replicationFactor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

