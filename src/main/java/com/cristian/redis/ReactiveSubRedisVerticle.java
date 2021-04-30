package com.cristian.redis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class ReactiveSubRedisVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        RedisConfig config = new RedisConfig();

        try (var redisAPIProducer = new RedisAPIProducer(config, vertx)) {
            redisAPIProducer.getRedisAPIContainer("HelloWorld");
        }
    }

}