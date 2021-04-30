package com.cristian.redis;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RedisAPIProducer implements AutoCloseable {
    private static final Map<String, RedisAPIContainer> REDIS_APIS = new ConcurrentHashMap<>();

    private final Vertx vertx;
    private final RedisConfig redisConfig;

    public RedisAPIProducer(RedisConfig redisConfig, Vertx vertx) {
        this.redisConfig = redisConfig;
        this.vertx = vertx;
    }

    public RedisAPIContainer getRedisAPIContainer(String name) {
        return REDIS_APIS.computeIfAbsent(name, new Function<>() {
            @Override
            public RedisAPIContainer apply(String s) {
                var redisConfiguration = RedisClientUtil.getConfiguration(RedisAPIProducer.this.redisConfig, name);

                Redis redis = createClient(s, redisConfiguration);
                RedisAPI redisAPI = getRedisAPI(redis, redisConfiguration.subMode);

                return new RedisAPIContainer(redis, redisAPI);
            }
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

    private RedisAPI getRedisAPI(Redis redis, boolean subscriberMode) {
        if (subscriberMode) {
            return getSingleConnectionClient(redis);
        } else {
            return getPooledClient(redis);
        }
    }

    private RedisAPI getPooledClient(Redis client) {
        return RedisAPI.api(client);
    }

    private RedisAPI getSingleConnectionClient(Redis client) {
        var subscriberClient = new RedisSubscriberClient(client);
        return RedisAPI.api(subscriberClient.getConnection());
    }

    @Override
    public void close() {
        for (RedisAPIContainer container : REDIS_APIS.values()) {
            container.close();
        }

        REDIS_APIS.clear();
    }

}
