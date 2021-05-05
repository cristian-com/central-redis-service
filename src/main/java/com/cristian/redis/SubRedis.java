package com.cristian.redis;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.*;
import io.vertx.redis.client.impl.types.PushType;

import java.util.List;

public class SubRedis {

    private static final int MAX_RECONNECT_RETRIES = 16;

    private final RedisConfig config;
    private final RedisOptions options;
    private final Vertx vertx;

    public SubRedis(RedisConfig config, Vertx vertx) throws Exception {
        this.vertx = vertx;
        this.config = config;
        this.options = RedisClientUtil.buildSubscriber(config.defaultClient);

        createRedisClient()
                .onSuccess(
                        conn -> {
                            var api = RedisAPI.api(conn);

                            conn.handler(
                                    response -> {
                                        if (response instanceof PushType notification) {
                                            for (Response val : notification) {
                                                System.out.println(val.toString());
                                            }
                                        }
                                    });

                            vertx
                                    .eventBus()
                                    .<JsonObject>consumer("io.vertx.redis.__keyevent@0__:*")
                                    .handler(
                                            msg -> {
                                                if (msg != null) System.out.println(msg.body());
                                            });

                            api.psubscribe(List.of("__keyevent@0__:*"))
                                    .onComplete(
                                            response -> {
                                                System.out.println(response);
                                            });
                        })
                .onComplete(res -> System.out.println(res));
    }

    /**
     * Will create a redis client and setup a reconnect handler when there is an exception in the
     * connection.
     */
    private Future<RedisConnection> createRedisClient() {
        Promise<RedisConnection> promise = Promise.promise();

        Redis.createClient(vertx, options)
                .connect()
                .onSuccess(
                        conn -> {
                            conn.exceptionHandler(
                                    e -> {
                                        attemptReconnect(0);
                                    });
                            promise.complete(conn);
                        });

        return promise.future();
    }

    /**
     * Attempt to reconnect up to MAX_RECONNECT_RETRIES
     */
    private void attemptReconnect(int retry) {
        if (retry > MAX_RECONNECT_RETRIES) {
            // we should stop now, as there's nothing we can do.
        } else {
            long backoff = (long) (Math.pow(2, Math.min(retry, 10)) * 10);

            vertx.setTimer(
                    backoff,
                    timer -> {
                        createRedisClient().onFailure(t -> attemptReconnect(retry + 1));
                    });
        }
    }
}
