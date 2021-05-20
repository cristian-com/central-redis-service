package com.cristian.redis;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class RedisMessageCodec implements MessageCodec<JsonObject, SubMessage> {
    @Override
    public void encodeToWire(Buffer buffer, JsonObject jsonObject) {
    }

    @Override
    public SubMessage decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public SubMessage transform(JsonObject jsonObject) {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public byte systemCodecID() {
        return 0;
    }
}
