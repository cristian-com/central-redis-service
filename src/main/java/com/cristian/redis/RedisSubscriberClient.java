package com.cristian.redis;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import io.vertx.redis.client.impl.RedisStandaloneConnection;

import java.util.List;
import java.util.Objects;

public class RedisSubscriberClient implements Redis {

    private final Redis redis;
    private RedisStandaloneConnection connection;

    public RedisSubscriberClient(Redis redis) {
        this.redis = redis;
    }

    @Override
    public Future<RedisConnection> connect() {
        var promise = Promise.<RedisConnection>promise();

        if (Objects.isNull(connection)) {
            redis.connect()
                    .onComplete(result -> {
                        System.out.println(result.failed());
                    })
                    .onSuccess(conn -> {
                        if (conn instanceof RedisStandaloneConnection standaloneConnection) {
                            connection = standaloneConnection;
                            promise.complete(standaloneConnection);
                        } else {
                            promise.fail("The returned connection is not standalone");
                        }
                    });
        } else {
            promise.complete(connection);
        }

        return promise.future();
    }

    @Override
    public void close() {
        redis.close();
    }

    @Override
    public Future<Response> send(Request command) {
        return redis.send(command);
    }

    @Override
    public Future<List<Response>> batch(List<Request> commands) {
        return redis.batch(commands);
    }

    public RedisStandaloneConnection getConnection() {
        return connection;
    }
}
