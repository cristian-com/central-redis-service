package com.cristian.redis.examples;

import com.cristian.redis.RedisAPIContainer;
import com.cristian.redis.RedisAPIProducer;
import com.cristian.redis.RedisConfig;
import com.cristian.redis.RedisSubscriber;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.redis.client.RedisClientType;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

public class SingleSubscriberVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var container = client("7000");
        var subscriber = new RedisSubscriber(vertx, container);
        var exampleConsumer = new ExampleMessageConsumer();
        subscriber.addChannelHandler(exampleConsumer);

        subscriber.start();
    }

    public RedisAPIContainer client(String port) {
        RedisConfig config = new RedisConfig();
        var redisAPIProducer = new RedisAPIProducer(config, vertx);
        config.defaultClient.clientType = RedisClientType.STANDALONE;
        config.defaultClient.maxPoolWaiting = 10;
        config.defaultClient.maxPoolSize = 10;
        config.defaultClient.connectionRetries = 2;
        config.defaultClient.subMode = true;
        config.defaultClient.hosts = Optional.of(Set.of(URI.create("redis://localhost:" + port)));

        return redisAPIProducer.getRedisAPIContainer("HelloWorld" + port);
    }
}