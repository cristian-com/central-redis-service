package com.cristian.redis;

import io.vertx.redis.client.RedisClientType;
import io.vertx.redis.client.RedisReplicas;
import io.vertx.redis.client.RedisRole;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RedisConfig {

    /**
     * The default redis client
     */
    public RedisConfiguration defaultClient = new RedisConfiguration();

    /**
     * Configures additional Redis client connections.
     * <p>
     * Each client has a unique identifier which must be identified to select the right connection.
     * For example:
     * <p>
     *
     * <pre>
     * quarkus.redis.client1.hosts = redis://localhost:6379
     * quarkus.redis.client2.hosts = redis://localhost:6380
     * </pre>
     * <p>
     * <p>
     *
     * <pre>
     * {@code
     * &#64;RedisClientName("client1")
     * &#64;Inject
     * RedisClient redisClient1
     * }
     * </pre>
     */
    Map<String, RedisConfiguration> additionalRedisClients;

    public static class RedisConfiguration {
        /**
         * The redis hosts to use while connecting to the redis server. Only the cluster mode will consider more than
         * 1 element.
         * <p>
         * The URI provided uses the following schema `redis://[username:password@][host][:port][/database]`
         *
         * @see <a href="https://www.iana.org/assignments/uri-schemes/prov/redis">Redis scheme on www.iana.org</a>
         */
        public Optional<Set<URI>> hosts = Optional.of(Set.of(URI.create("redis://localhost:6379")));

        /**
         * The maximum delay to wait before a blocking command to redis server times out
         */
        public Optional<Duration> timeout = Optional.of(Duration.ofSeconds(10));

        /**
         * The redis client type
         */
        public RedisClientType clientType = RedisClientType.STANDALONE;

        /**
         * The master name (only considered in HA mode).
         */
        public Optional<String> masterName = Optional.of("mymaster");

        /**
         * The role name (only considered in HA mode).
         */

        public Optional<RedisRole> role = Optional.of(RedisRole.MASTER);

        /**
         * Whether or not to use replicas nodes (only considered in Cluster mode).
         */
        public Optional<RedisReplicas> replicas = Optional.empty();

        /**
         * The maximum size of the connection pool. When working with cluster or sentinel.
         * <p>
         * This value should be at least the total number of cluster member (or number of sentinels + 1)
         */
        public int maxPoolSize = 1;

        /**
         * The maximum waiting requests for a connection from the pool.
         */
        public int maxPoolWaiting = 1;

        /**
         * The duration indicating how often should the connection pool cleaner executes.
         */
        public Optional<Duration> poolCleanerInterval = Optional.empty();

        /**
         * The timeout for a connection recycling.
         */
        public Duration poolRecycleTimeout = Duration.ofSeconds(15);

        /**
         * Sets how much handlers is the client willing to queue.
         * <p>
         * The client will always work on pipeline mode, this means that messages can start queueing.
         * Using this configuration option, you can control how much backlog you're willing to accept.
         */
        public int maxWaitingHandlers = 2048;

        /**
         * Tune how much nested arrays are allowed on a redis response. This affects the parser performance.
         */
        public int maxNestedArrays = 32;
    }
}
