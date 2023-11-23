package app.consistent_hashing;

import app.NodeInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashFunction implements HashFunction {
    @Override
    public int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(key.getBytes());
            return ((bytes[0] & 0xFF) * 256 + (bytes[1] & 0xFF)) % 256;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm for hash function");
        }
    }
}
