package com.cristian.redis;

import io.vertx.redis.client.RedisClientType;
import io.vertx.redis.client.RedisOptions;

import java.net.URI;
import java.util.Set;

public class RedisClientUtil {
    public static final String DEFAULT_CLIENT = "<default>";

    public static RedisOptions buildSubscriber(RedisConfig.RedisConfiguration redisConfig) {
        RedisOptions options = new RedisOptions();
        options.setType(redisConfig.clientType);

        if (RedisClientType.STANDALONE == redisConfig.clientType) {
            if (redisConfig.hosts.isPresent() && redisConfig.hosts.get().size() > 1) {
                throw new IllegalArgumentException("Multiple hosts supplied for non clustered configuration");
            }
        }

        if (redisConfig.hosts.isPresent()) {
            Set<URI> hosts = redisConfig.hosts.get();
            for (URI host : hosts) {
                options.addConnectionString(host.toString());
            }
        }

        options.setMaxNestedArrays(redisConfig.maxNestedArrays);
        options.setMaxWaitingHandlers(redisConfig.maxWaitingHandlers);
        options.setMaxPoolSize(redisConfig.maxPoolSize);
        options.setMaxPoolWaiting(redisConfig.maxPoolWaiting);

        redisConfig.role.ifPresent(options::setRole);
        redisConfig.masterName.ifPresent(options::setMasterName);
        redisConfig.replicas.ifPresent(options::setUseReplicas);

        return options;
    }

    public static RedisOptions buildOptions(RedisConfig.RedisConfiguration redisConfig) {
        RedisOptions options = new RedisOptions();
        options.setType(redisConfig.clientType);

        if (RedisClientType.STANDALONE == redisConfig.clientType) {
            if (redisConfig.hosts.isPresent() && redisConfig.hosts.get().size() > 1) {
                throw new IllegalArgumentException("Multiple hosts supplied for non clustered configuration");
            }
        }

        if (redisConfig.hosts.isPresent()) {
            Set<URI> hosts = redisConfig.hosts.get();
            for (URI host : hosts) {
                options.addConnectionString(host.toString());
            }

        }
        options.setMaxNestedArrays(redisConfig.maxNestedArrays);
        options.setMaxWaitingHandlers(redisConfig.maxWaitingHandlers);
        options.setMaxPoolSize(redisConfig.maxPoolSize);
        options.setMaxPoolWaiting(redisConfig.maxPoolWaiting);
        options.setPoolRecycleTimeout(Math.toIntExact(redisConfig.poolRecycleTimeout.toMillis()));

        redisConfig.poolCleanerInterval
                .ifPresent(duration -> options.setPoolCleanerInterval(Math.toIntExact(duration.toMillis())));
        redisConfig.role.ifPresent(options::setRole);
        redisConfig.masterName.ifPresent(options::setMasterName);
        redisConfig.replicas.ifPresent(options::setUseReplicas);

        return options;
    }

    public static boolean isDefault(String clientName) {
        return DEFAULT_CLIENT.equals(clientName);
    }

    public static RedisConfig.RedisConfiguration getConfiguration(RedisConfig config, String name) {
        return new RedisConfig.RedisConfiguration();
    }
}
