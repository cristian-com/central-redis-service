package com.cristian.redis.examples;

import com.cristian.redis.ChannelConsumer;
import com.cristian.redis.RedisSubscriber;

public class ExampleMessageConsumer extends ChannelConsumer<ExampleMessage> {

    private static final String channel = "example";

    public ExampleMessageConsumer(RedisSubscriber subscriber, String channel) {
        super(subscriber, channel);
    }

    @Override
    public void handle(ExampleMessage message) {

    }

    @Override
    protected Class<ExampleMessage> getClazz() {
        return ExampleMessage.class;
    }

    @Override
    protected String getChannelName() {
        return channel;
    }

}
