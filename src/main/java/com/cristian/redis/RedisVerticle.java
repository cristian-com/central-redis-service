package com.cristian.redis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.*;

import java.util.List;

public class RedisVerticle extends AbstractVerticle {

    private static final int MAX_RECONNECT_RETRIES = 16;

    private RedisOptions options;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.options = RedisClientUtil.buildSubscriber(new RedisConfig().defaultClient);

        createRedisClient().onSuccess(conn -> {
            var api = RedisAPI.api(conn);

            conn.handler(message -> {
                System.out.println(message);
            });

            api.psubscribe(List.of("__keyevent@0__:*"))
                    .onComplete(handler ->
                            System.out.println(handler.result().toString())
                    );
        });
    }

    /**
     * Will create a redis client and setup a reconnect handler when there is
     * an exception in the connection.
     */
    private Future<RedisConnection> createRedisClient() {
        Promise<RedisConnection> promise = Promise.promise();

        Redis.createClient(vertx, options)
                .connect()
                .onComplete(result -> {
                    System.out.println(result.failed());
                })
                .onSuccess(conn -> {
                    // make sure the client is reconnected on error
                    conn.exceptionHandler(e -> {
                        // attempt to reconnect,
                        // if there is an unrecoverable error
                        attemptReconnect(0);
                    });
                    // allow further processing
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
            // retry with backoff up to 10240 ms
            long backoff = (long) (Math.pow(2, Math.min(retry, 10)) * 10);

            vertx.setTimer(backoff, timer -> {
                createRedisClient()
                        .onFailure(t -> attemptReconnect(retry + 1));
            });
        }
    }
}