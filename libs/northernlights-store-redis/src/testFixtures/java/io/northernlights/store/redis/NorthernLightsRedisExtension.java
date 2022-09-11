package io.northernlights.store.redis;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class NorthernLightsRedisExtension implements Extension, BeforeAllCallback, AfterAllCallback {
    private final NorthernLightsRedisContainer redisContainer = new NorthernLightsRedisContainer();

    @Override
    public void beforeAll(ExtensionContext context) {
        redisContainer.start();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        redisContainer.stop();
    }
}
