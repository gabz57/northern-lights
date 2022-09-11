package io.northernlights.store.redis;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class NorthernLightsRedisContainer extends GenericContainer<NorthernLightsRedisContainer> {

    public NorthernLightsRedisContainer() {
        super(DockerImageName.parse("redis:7.0.4-alpine"));
        withExposedPorts(6379);
    }

    @Override
    public void start() {
        super.start();

        System.setProperty("chat.redis.hostname", this.getHost());
        System.setProperty("chat.redis.port", this.getMappedPort(6379).toString());
    }

    @Override
    public void stop() {
        super.stop();
    }
}
