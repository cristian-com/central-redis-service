package com.cristian.redis;

import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.impl.RedisClient;

class RedisAPIContainer {
    private final Redis redis;

    private final RedisAPI redisAPI;

    private final RedisClient redisClient;

    public RedisAPIContainer(Redis redis, RedisAPI redisAPI, RedisClient redisClient) {
        this.redis = redis;
        this.redisAPI = redisAPI;
        this.redisClient = redisClient;
    }

    public Redis getRedis() {
        return redis;
    }

    public RedisAPI getRedisAPI() {
        return redisAPI;
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public void close() {
        this.redisAPI.close();
        this.redis.close();
        this.redisAPI.close();
        this.redisClient.close();
    }
}
