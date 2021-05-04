package com.cristian.redis.examples;

import com.cristian.redis.RedisAPIProducer;
import com.cristian.redis.RedisConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisClientType;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SingleSubscriberVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx
                .eventBus()
                .<JsonObject>consumer("io.vertx.redis.__keyevent@0__:*")
                .handler(
                        msg -> {
                            if (msg != null) System.out.println(msg.body());
                        });


        clientOne();
        clientTwo();
    }

    public void clientTwo() {
        RedisConfig config = new RedisConfig();
        var redisAPIProducer = new RedisAPIProducer(config, vertx);
        config.defaultClient.clientType = RedisClientType.STANDALONE;
        config.defaultClient.maxPoolWaiting = 10;
        config.defaultClient.maxPoolSize = 10;
        config.defaultClient.connectionRetries = 2;
        config.defaultClient.subMode = true;
        config.defaultClient.hosts = Optional.of(Set.of(URI.create("redis://localhost:6502")));

        var container = redisAPIProducer.getRedisAPIContainer("HelloWorld");

        container.getRedisAPI().psubscribe(List.of("__keyevent@0__:*"));
    }

    public void clientOne() {
        RedisConfig config = new RedisConfig();
        var redisAPIProducer = new RedisAPIProducer(config, vertx);
        config.defaultClient.clientType = RedisClientType.STANDALONE;
        config.defaultClient.maxPoolWaiting = 10;
        config.defaultClient.maxPoolSize = 10;
        config.defaultClient.connectionRetries = 2;
        config.defaultClient.subMode = true;
        config.defaultClient.hosts = Optional.of(Set.of(URI.create("redis://localhost:6503")));

        var container = redisAPIProducer.getRedisAPIContainer("HelloWorld");

        container.getRedisAPI().psubscribe(List.of("__keyevent@0__:*"));
    }
}