package com.cristian.redis;

import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

public class RedisAPIContainer {

    private final RedisAPI redisAPI;
    private final Redis redis;

    public RedisAPIContainer(Redis redis, RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
        this.redis = redis;
    }

    public RedisAPI getRedisAPI() {
        return redisAPI;
    }

    public Redis getRedis() {
        return redis;
    }

    public void close() {
        this.redisAPI.close();
    }
}
