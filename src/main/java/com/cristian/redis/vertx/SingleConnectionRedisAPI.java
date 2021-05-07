package com.cristian.redis.vertx;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;

import java.util.Objects;

public class SingleConnectionRedisAPI extends BaseRedisAPI {

    private final Redis redis;
    private boolean onError = false;
    private RedisConnection connection;

    public SingleConnectionRedisAPI(Redis redis) {
        Objects.requireNonNull(redis);
        this.redis = redis;
    }

    public Future<RedisConnection> getConnection() {
        final Promise<RedisConnection> promise = Promise.promise();

        if (Objects.nonNull(connection)) {
            promise.complete(connection);
        } else {
            redis.connect()
                    .onSuccess(conn -> {
                        connection = conn;
                        onError = false;

                        conn.exceptionHandler(e -> {
                            System.out.println(connection);
                            System.out.println("Hello Muchachos");
                        });
                        promise.complete(conn);
                    })
                    .onFailure(error -> {
                        onError = true;
                        promise.fail(error);
                    });
        }

        return promise.future();
    }

    @Override
    void send(Request req, Promise<Response> promise) {
        if (Objects.nonNull(connection)) {
            connection.send(req, promise);
        } else {
            getConnection()
                    .onSuccess(conn -> {
                        conn.send(req, promise);
                    })
                    .onFailure(error -> {
                        System.out.println("Hello Muchachos");
                        promise.fail(error);
                    });
        }
    }

    @Override
    public void close() {
        if (Objects.nonNull(connection)) {
            connection.close();
        } else {
            System.out.println("API not yet connected");
        }
    }
}
