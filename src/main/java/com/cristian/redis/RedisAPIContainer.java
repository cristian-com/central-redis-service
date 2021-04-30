package com.cristian.redis;

import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

class RedisAPIContainer {
    private final Redis redis;
    private RedisAPI redisAPI;
    private 

    public RedisAPIContainer(Redis redis, RedisAPI redisAPI) {
        this.redis = redis;
        this.redisAPI = redisAPI;
    }

    public Redis getRedis() {
        return redis;
    }

    public RedisAPI getRedisAPI() {
        return redisAPI;
    }

    public void close() {
        this.redisAPI.close();
        this.redis.close();
        this.redisAPI.close();
    }
}
