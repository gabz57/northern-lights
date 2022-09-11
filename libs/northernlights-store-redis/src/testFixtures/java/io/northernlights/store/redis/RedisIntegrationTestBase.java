package io.northernlights.store.redis;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(NorthernLightsRedisExtension.class)
public class RedisIntegrationTestBase {
    // mainly for inspiration, redis rather not tested alone, you should probably use
    // @ExtendWith(NorthernLightsRedisExtension.class)
}
