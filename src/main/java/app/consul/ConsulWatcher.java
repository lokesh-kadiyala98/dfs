package app.consul;

import com.orbitz.consul.Consul;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.model.health.ServiceHealth;
import com.google.common.net.HostAndPort;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ConsulWatcher {
    private final Consul consul;
    private ServiceHealthCache serviceHealthCache;

    public ConsulWatcher(String consulHost, int consulPort) {
        this.consul = Consul.builder()
            .withHostAndPort(HostAndPort.fromParts(consulHost, consulPort))
            .withReadTimeoutMillis(TimeUnit.SECONDS.toMillis(20))
            .build();
    }

    public void watchService(String serviceName, Consumer<ServiceHealth> callback) {
        serviceHealthCache = ServiceHealthCache.newCache(consul.healthClient(), serviceName);

        serviceHealthCache.addListener(newValues -> {
            newValues.values().forEach(callback);
        });

        serviceHealthCache.start();
    }

    public void stopWatching() {
        if (serviceHealthCache != null) {
            serviceHealthCache.stop();
        }
    }
}
