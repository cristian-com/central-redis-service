package com.cristian.redis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class ReactiveSubRedisVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        RedisConfig config = new RedisConfig();
        var redisAPIProducer = new RedisAPIProducer(config, vertx);

        var container = redisAPIProducer.getRedisAPIContainer("HelloWorld");
        System.out.println("Hello World");
/*
        ((SingleConnectionRedisAPI)container.getRedisAPI()).getConnection().onSuccess(connection ->{
            connection.handler(
                    response -> {
                        if (response instanceof PushType) {
                            PushType notification = (PushType) response;
                            for (Response val : notification) {
                                System.out.println(val.toString());
                            }
                        }
                    });
        });*/

        vertx
                .eventBus()
                .<JsonObject>consumer("io.vertx.redis.micanal*")
                .handler(
                        msg -> {
                            if (msg != null) System.out.println(msg.body());
                        });

        container.getRedisAPI().psubscribe(List.of("micanal*"))
                .onComplete(response -> {
                    System.out.println(response);
                })
                .onFailure(response -> {
                    System.out.println(response);
                });

        container.getRedisAPI().subscribe(List.of("micanal"))
                .onComplete(response -> {
                    System.out.println(response);
                })
                .onFailure(response -> {
                    System.out.println(response);
                });


        container.getRedisAPI().unsubscribe(List.of("micanal"))
                .onComplete(response -> {
                    System.out.println(response);
                })
                .onFailure(response -> {
                    System.out.println(response);
                });
    }

}