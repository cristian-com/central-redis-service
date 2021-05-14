package com.cristian.redis;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class RedisSubscriber implements AutoCloseable {

    static final String BASE_ADDRESS = "io.vertx.redis.message.";

    private final ChannelType channelType;
    private final String channel;

    public RedisSubscriber(Vertx vertx, String channel, ChannelType channelType) {
        this.channelType = channelType;
        this.channel = channel;

        vertx.eventBus()
                .<JsonObject>consumer(BASE_ADDRESS + channel)
                .handler(this::onMessage);
    }

    public void onMessage(Message<JsonObject> message) {
        System.out.println("Logging " + message);
        var object = objectFromMessage(message.body(), getClazz());
        Promise<Void> promise = Promise.promise();

        handleMessage(object);
    }

    protected Class<?> getClazz() {
        return JsonObject.class;
    }

    private <T> T objectFromMessage(JsonObject message, Class<T> mClass) {
        // Assuming message would be never null at this points, handle otherwise
        return message.mapTo(mClass);
    }

    public <T> Future<Void> handleMessage(T message) {
        Handler
    }

    public void close() {
        System.out.println("Not yet implemented");
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public String getChannel() {
        return channel;
    }

    enum ChannelType {
        SINGLE, PATTERN
    }

}
