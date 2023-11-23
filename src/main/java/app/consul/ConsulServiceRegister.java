package app.consul;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import org.json.JSONObject;

public class ConsulServiceRegister {
    private String ipAddress;
    private int portNo;

    public ConsulServiceRegister(String ipAddress, int portNo) {
        this.ipAddress = ipAddress;
        this.portNo = portNo;
    }

    public void registerService(String serviceName, String serviceId, String address, int port, String healthCheckUrl) {
        try {
            URL url = new URL("http://" + this.ipAddress + ":" + this.portNo + "/v1/agent/service/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject service = new JSONObject();
            service.put("Name", serviceName);
            service.put("ID", serviceId);
            service.put("Address", address);
            service.put("Port", port);

            JSONObject check = new JSONObject();
            check.put("http", healthCheckUrl);
            check.put("interval", "10s");
            service.put("Check", check);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(service.toString().getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to register service with Consul: HTTP error code : " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
