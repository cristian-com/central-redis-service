package com.cristian.redis.vertx;

import io.vertx.core.Promise;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;

import java.util.Objects;

public class PooledRedisAPI extends BaseRedisAPI {

    private final Redis redis;

    public PooledRedisAPI(Redis redis) {
        Objects.requireNonNull(redis);

        this.redis = redis;
    }

    @Override
    void send(Request req, Promise<Response> promise) {
        redis.send(req, promise);
    }

    @Override
    public void close() {
        redis.close();
    }
}
