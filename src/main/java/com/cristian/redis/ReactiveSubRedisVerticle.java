package com.cristian.redis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import java.util.List;

public class ReactiveSubRedisVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        RedisConfig config = new RedisConfig();
        var redisAPIProducer = new RedisAPIProducer(config, vertx);

        var container = redisAPIProducer.getRedisAPIContainer("HelloWorld");
        System.out.println("Hello World");

        container.getRedisAPI().psubscribe(List.of("__keyevent@0__:*"))
                .onComplete(response -> {
                    System.out.println(response);
                })
        .onFailure(response -> {
            System.out.println(response);
        });
    }

}