package com.cristian.redis.vertx;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;

public abstract class BaseRedisAPI implements RedisAPI {

    @Override
    public final Future<Response> send(Command cmd, String... args) {
        final Promise<Response> promise = Promise.promise();
        final Request req = Request.cmd(cmd);

        if (args != null) {
            for (String o : args) {
                if (o == null) {
                    req.nullArg();
                } else {
                    req.arg(o);
                }
            }
        }

        send(req, promise);

        return promise.future();
    }

    abstract void send(Request req, Promise<Response> promise);

}
