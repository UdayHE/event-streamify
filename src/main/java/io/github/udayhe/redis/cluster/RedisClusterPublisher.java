package io.github.udayhe.redis.cluster;


import io.github.udayhe.redis.RedisPublisher;
import io.github.udayhe.request.Message;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.sync.RedisClusterPubSubCommands;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.ObjectMapper;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static java.util.Objects.nonNull;

/**
 * @author udayhegde
 */

@Singleton
@Context
@Slf4j
@Requires(property = "redis.enable", value = StringUtils.TRUE)
public class RedisClusterPublisher implements RedisPublisher {

    private final StatefulRedisClusterPubSubConnection<String, String> redisConnection;

    private final ObjectMapper objectMapper;

    public RedisClusterPublisher(RedisClusterClient redisClusterClient, ObjectMapper objectMapper) {
        this.redisConnection = redisClusterClient.connectPubSub();
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishMessage(String topic, Message message) throws IOException {
        if (nonNull(redisConnection)) {
            RedisClusterPubSubCommands<String, String> sync = redisConnection.sync();
            sync.publish(topic, objectMapper.writeValueAsString(message));
            log.info("RedisClusterPublisher, published message.");
        }
    }

    @PreDestroy
    public void close() {
        if (redisConnection != null)
            redisConnection.close();
    }
}
