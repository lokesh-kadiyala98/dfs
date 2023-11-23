package app.consistent_hashing;

import app.NodeInfo;

public interface HashFunction {
    int hash(String key);
}
