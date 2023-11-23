package app;

public class NodeInfo {
    private String nodeId;
    private String ipAddress;
    private int portNo;

    public NodeInfo(String nodeId, String ipAddress, int portNo) {
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.portNo = portNo;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "dfs.NodeInfo{" +
            "nodeId='" + nodeId + '\'' +
            ", portNo=" + portNo +
            '}';
    }
}
