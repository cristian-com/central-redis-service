package com.cristian.redis;

import io.vertx.core.Handler;

public abstract class ChannelConsumer<T> implements Handler<T> {

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
}
