package app.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HealthCheckServer {
    private HttpServer server;

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", exchange -> {
            String response = "OK";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
