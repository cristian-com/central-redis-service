package com.cristian.redis.examples;

import com.cristian.redis.ChannelConsumer;

public class ExampleMessageConsumer extends ChannelConsumer<ExampleMessage> {

    private static final String channel = "example";

    @Override
    public void handle(ExampleMessage message) {
        System.out.println("The message " + message);
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
