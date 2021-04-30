package com.cristian.redis;

import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisConnection;

import java.util.Objects;

public class RedisSubscriberClient {

    private final Redis redis;
    private RedisConnection connection;

    public RedisSubscriberClient(Redis redis) {
        this.redis = redis;

        connect();
    }

    public void connect() {
        if (Objects.isNull(connection)) {
            redis.connect()
                    .onComplete(result -> {
                        System.out.println(result.failed());
                    })
                    .onSuccess(conn -> {
                        connection = conn;
                    });
        }
    }

    public RedisConnection getConnection() {
        return connection;
    }
}
