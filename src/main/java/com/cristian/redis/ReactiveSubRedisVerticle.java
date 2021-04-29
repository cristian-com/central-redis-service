package com.cristian.redis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class ReactiveSubRedisVerticle extends AbstractVerticle {

    private SubRedis subRedis;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        RedisConfig config = new RedisConfig();

        subRedis = new SubRedis(config,
                vertx);
    }

}