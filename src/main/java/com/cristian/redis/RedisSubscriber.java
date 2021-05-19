package com.cristian.redis;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Response;
import io.vertx.redis.client.impl.types.Multi;

import java.util.HashMap;
import java.util.Map;

public class RedisSubscriber implements AutoCloseable {

    private static final String BASE_ADDRESS = "rd.message.";
    private static final String SUBSCRIBE_BASE_ADDRESS = BASE_ADDRESS + "subscribe";
    private static final String UNSUBSCRIBE_BASE_ADDRESS = BASE_ADDRESS + "unsubscribe";

    private final String channel;
    private final Vertx vertx;
    private final EventBus eventBus;
    private final RedisAPIContainer redisAPIContainer;
    private final Map<String, Handler<?>> patternHandlers = new HashMap<>();
    private final Map<String, Handler<?>> channelHandlers = new HashMap<>();

    public RedisSubscriber(Vertx vertx, String channel, RedisAPIContainer redisAPIContainer) {
        this.channel = channel;
        this.vertx = vertx;
        this.eventBus = vertx.eventBus();
        this.redisAPIContainer = redisAPIContainer;
    }

    public void start() {
        vertx.eventBus()
                .<JsonObject>consumer(BASE_ADDRESS + channel)
                .handler(this::onMessage);
    }

    private void onMessage(Message<JsonObject> message) {
        redisAPIContainer.getRedis();
    }

    public void close() {
        System.out.println("Not yet implemented");
    }

    private void handle(Response response) {
        int responseSize = 0;
        final String address;
        final Object event;
        final String typeMessage;

        // pub/sub messages are arrays
        if (response instanceof Multi pushResponse) {
            typeMessage = pushResponse.get(0).toString();
            // Detect valid published messages according to https://redis.io/topics/pubsubs
            switch (typeMessage) {
                case "pmessage":
                    if (response.size() == 4) {
                        handleMessage(new SubMessage(response.get(3).toString(),
                                response.get(2).toString(),
                                response.get(1).toString()));
                    }
                    break;
                case "message":
                    if (response.size() == 4) {
                        handleMessage(new SubMessage(response.get(2).toString(),
                                response.get(1).toString(),
                                null));
                    }
                    break;
                case "subscribe":
                case "psubscribe":
                    responseSize = 3;
                    address = SUBSCRIBE_BASE_ADDRESS;

                    break;
                case "unpsubscribe":
                case "unsubscribe":
                    responseSize = 3;
                    address = UNSUBSCRIBE_BASE_ADDRESS;
                    break;
                default:
                    System.out.println("No handler");
            }

        }
    }

    private void handleMessage(SubMessage message) {
        DeliveryOptions deliveryOptions = new DeliveryOptions()
                .setCodecName("rd.message.codec" + message.channel());

        Handler<?> handler = channelHandlers.getOrDefault(message.channel(),
                patternHandlers.get(message.pattern()));

        eventBus.publish(BASE_ADDRESS + "message" + message.channel(),
                message, deliveryOptions);
    }

    private void handleChannelSubscription(String channel) {
        System.out.println("Subscribed to channel" + channel);
    }

    private void handlePatternSubscription(String channel, String pattern){

    }
}
