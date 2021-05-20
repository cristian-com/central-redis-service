package com.cristian.redis;

import com.cristian.redis.vertx.SingleConnectionRedisAPI;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Response;
import io.vertx.redis.client.impl.types.Multi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisSubscriber implements AutoCloseable {

    private static final String BASE_ADDRESS = "rd.message.";
    private static final String SUBSCRIBE_BASE_ADDRESS = BASE_ADDRESS + "subscribe";
    private static final String UNSUBSCRIBE_BASE_ADDRESS = BASE_ADDRESS + "unsubscribe";

    private final Vertx vertx;
    private final EventBus eventBus;
    private final RedisAPIContainer redisAPIContainer;
    private final SingleConnectionRedisAPI redisAPI;
    private final Map<String, ConsumerEntry> patterns = new ConcurrentHashMap<>();
    private final Map<String, ConsumerEntry> channels = new ConcurrentHashMap<>();

    private boolean running = false;

    public RedisSubscriber(Vertx vertx, RedisAPIContainer redisAPIContainer) {
        this.vertx = vertx;
        this.eventBus = vertx.eventBus();
        this.redisAPIContainer = redisAPIContainer;

        if (redisAPIContainer.getRedisAPI() instanceof SingleConnectionRedisAPI singleConnectionRedisAPI) {
            this.redisAPI = singleConnectionRedisAPI;
        } else {
            throw new IllegalArgumentException("Pooled clients not supported");
        }
    }

    public void start() {
        subscribe(patterns.keySet().stream().toList());

        redisAPI.getConnection()
                .onSuccess(conn -> conn.handler(this::handlePubMessage));

        running = true;
    }

    public void close() {
        System.out.println("Not yet implemented");
        running = false;
    }

    private void subscribe(List<String> channels) {
        redisAPIContainer.getRedisAPI()
                .subscribe(channels);
    }

    private boolean handlePmessage(Multi response) {
        if (response.size() == 4) {
            return false;
        }

        final String channel = response.get(3).toString();
        final JsonObject message = new JsonObject(response.get(2).toString());
        final String pattern = response.get(1).toString();

        DeliveryOptions deliveryOptions = new DeliveryOptions()
                .setCodecName("rd.message.codec" + channel);

        eventBus.publish(BASE_ADDRESS + "message" + channel, message, deliveryOptions);

        return true;
    }

    private boolean handleMessage(Multi response) {
        if (response.size() == 3) {
            return false;
        }

        final String channel = response.get(1).toString();
        final JsonObject message = new JsonObject(response.get(2).toString());

        DeliveryOptions deliveryOptions = new DeliveryOptions()
                .setCodecName("rd.message.codec" + channel);

        eventBus.publish(BASE_ADDRESS + "message" + channel, message, deliveryOptions);

        return true;
    }

    public <T> void addChannelHandler(String channel, ChannelConsumer<T> handler) {
        if (running) {
            subscribe(List.of(channel));
        }

        var consumer = eventBus
                .consumer(BASE_ADDRESS + "message" + channel)
                .handler(msg -> handler.handleInternal(msg.body()));

        channels.put(channel, new ConsumerEntry(handler, consumer, false));
    }

    private boolean handleChannelSubscription(Multi response) {
        final String channel = "";
        System.out.println("Subscribed to channel" + channel);
        var entry = channels.get(channel);
        entry.handler().onSubscribed();
        return true;
    }

    private boolean handlePatternSubscription(Multi response) {
        return true;
    }

    private void handlePubMessage(Response response) {
        final String typeMessage;
        boolean handled;

        // pub/sub messages are arrays
        if (response instanceof Multi pushResponse) {
            typeMessage = pushResponse.get(0).toString();
            // Detect valid published messages according to https://redis.io/topics/pubsubs
            handled = switch (typeMessage) {
                case "pmessage" -> handlePmessage(pushResponse);
                case "message" -> handleMessage(pushResponse);
                case "subscribe" -> handleChannelSubscription(pushResponse);
                case "psubscribe" -> handlePatternSubscription(pushResponse);
                default -> false;
            };

            if (!handled) {
                System.out.println("Unknown response type " + typeMessage);
            }
        }
    }

    record ConsumerEntry(ChannelConsumer<?> handler, MessageConsumer<?> consumer,
                         boolean connected) {
        ConsumerEntry {
            connected = false;
        }

        public ConsumerEntry(boolean connected) {
            this(null, null, connected);
        }
    }

}
