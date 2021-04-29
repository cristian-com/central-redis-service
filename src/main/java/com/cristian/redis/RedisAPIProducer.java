package com.cristian.redis;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.impl.RedisClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RedisAPIProducer {
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
                Duration timeout = Duration.ofSeconds(10);
                RedisConfig.RedisConfiguration redisConfiguration = RedisClientUtil.getConfiguration(RedisAPIProducer.this.redisConfig,
                        name);

                if (redisConfiguration.timeout.isPresent()) {
                    timeout = redisConfiguration.timeout.get();
                }

                RedisOptions options = RedisClientUtil.buildOptions(redisConfiguration);
                Redis redis = Redis.createClient(vertx, options);
                RedisAPI redisAPI = RedisAPI.api(redis);


                RedisClient redisClient = null;//= new RedisClientImpl(redisAPI, timeout);
                return new RedisAPIContainer(redis, redisAPI, redisClient);
            }
        });
    }

    //@PreDestroy
    public void close() {
        for (RedisAPIContainer container : REDIS_APIS.values()) {
            container.close();
        }

        REDIS_APIS.clear();
    }

}
