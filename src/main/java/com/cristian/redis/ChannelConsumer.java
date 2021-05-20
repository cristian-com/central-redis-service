package com.cristian.redis;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public abstract class ChannelConsumer<T> implements Handler<T>,
        MessageCodec<JsonObject, T> {

    private boolean automaticStart = true;

    public ChannelConsumer(RedisSubscriber subscriber,
                           String channel, boolean automaticStart) {
        this(subscriber, channel);
        this.automaticStart = automaticStart;
    }

    public ChannelConsumer(RedisSubscriber subscriber,
                           String channel) {
        subscriber.addChannelHandler(channel, this);
    }

    @SuppressWarnings("unchecked")
    protected void handleInternal(Object message) {
        try {
            this.handle((T) message);
        } catch (ClassCastException e) {
            System.out.println("Consumer is receiving the wrong message type.");
        }
    }

    protected void onSubscribed() {
        System.out.println("Subscribed");
    }

    protected void onUnsubscribed() {
        System.out.println("Unsubscribed");
    }

    @Override
    public void encodeToWire(Buffer buffer, JsonObject jsonObject) {
        Buffer encoded = jsonObject.toBuffer();
        buffer.appendInt(encoded.length());
        buffer.appendBuffer(encoded);
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        var json = new JsonObject(buffer.slice(pos, pos + length));
        return json.mapTo(getClazz());
    }

    @Override
    public T transform(JsonObject jsonObject) {
        return jsonObject.mapTo(getClazz());
    }

    @Override
    public final String name() {
        return getChannelName();
    }

    protected abstract Class<T> getClazz();

    protected abstract String getChannelName();

    @Override
    public final byte systemCodecID() {
        return -1;
    }
}
