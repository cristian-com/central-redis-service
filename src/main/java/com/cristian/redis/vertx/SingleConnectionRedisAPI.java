package com.cristian.redis.vertx;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;

import java.util.Objects;

public class SingleConnectionRedisAPI extends BaseRedisAPI {

    private boolean onError = false;
    private final Future<RedisConnection> connFuture;
    private RedisConnection connection;

    public SingleConnectionRedisAPI(Redis redis) {
        Objects.requireNonNull(redis);
        connFuture = redis.connect();

        connFuture
                .onComplete(
                        result -> {
                            if (result.failed()) {
                                onError = true;
                            }
                        })
                .onSuccess(
                        conn -> {
                            connection = conn;
                            onError = false;
                        });
    }

    public Future<RedisConnection> getConnection() {
        return connFuture;
    }

    @Override
    void send(Request req, Promise<Response> promise) {
        if (connFuture.isComplete()) {
            connection.send(req, promise);
        } else {
            connFuture.onSuccess(connection -> connection.send(req, promise));
            connFuture.onFailure(promise::fail);
        }
    }

    @Override
    public void close() {
        connection.close();
    }
}
