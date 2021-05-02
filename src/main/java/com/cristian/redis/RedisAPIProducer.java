package com.cristian.redis;

import com.cristian.redis.vertx.PooledRedisAPI;
import com.cristian.redis.vertx.SingleConnectionRedisAPI;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisAPIProducer implements AutoCloseable {
    private static final Map<String, RedisAPIContainer> REDIS_APIS = new ConcurrentHashMap<>();

    private final Vertx vertx;
    private final RedisConfig redisConfig;

    public RedisAPIProducer(RedisConfig redisConfig, Vertx vertx) {
        this.redisConfig = redisConfig;
        this.vertx = vertx;
    }

    public RedisAPIContainer getRedisAPIContainer(String name) {
        return REDIS_APIS.computeIfAbsent(
                name,
                s -> {
                    var redisConfiguration =
                            RedisClientUtil.getConfiguration(this.redisConfig, name);

                    Redis redis = createClient(name, redisConfiguration);
                    RedisAPI redisAPI;

                    if (redisConfiguration.subMode) {
                        redisAPI = new SingleConnectionRedisAPI(redis);
                    } else {
                        redisAPI = new PooledRedisAPI(redis);
                    }

                    return new RedisAPIContainer(redis, redisAPI);
                });
    }

    private Redis createClient(String name, RedisConfig.RedisConfiguration redisConfiguration) {
        var timeout = Duration.ofSeconds(10);

        if (redisConfiguration.timeout.isPresent()) {
            timeout = redisConfiguration.timeout.get();
        }

        var options = RedisClientUtil.buildOptions(redisConfiguration);

        return Redis.createClient(vertx, options);
    }

    @Override
    public void close() {
        for (RedisAPIContainer container : REDIS_APIS.values()) {
            container.close();
        }

        REDIS_APIS.clear();
    }
}
