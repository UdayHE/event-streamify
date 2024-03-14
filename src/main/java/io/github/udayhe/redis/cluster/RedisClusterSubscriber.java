package io.github.udayhe.redis.cluster;

import io.github.udayhe.factory.ListenerProcessingFactory;
import io.github.udayhe.request.Message;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import io.lettuce.core.cluster.pubsub.RedisClusterPubSubAdapter;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.sync.RedisClusterPubSubCommands;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import static io.github.udayhe.util.Constants.SSE_BROADCAST_TOPIC;
import static io.github.udayhe.util.Constants.WS_BROADCAST_TOPIC;
import static java.util.Objects.nonNull;

/**
 * @author udayhegde
 */
@Singleton
@Context
@Slf4j
@Requires(property = "redis.enable", value = StringUtils.TRUE)
public class RedisClusterSubscriber {

    private final StatefulRedisClusterPubSubConnection<String, String> redisConnection;
    private final ListenerProcessingFactory listenerProcessingFactory;
    private final ObjectMapper objectMapper;
    private final RedisClusterPubSubAdapter<String, String> listener = new CustomRedisPubSubAdapter();

    public RedisClusterSubscriber(RedisClusterClient redisClient,
                                  ListenerProcessingFactory listenerProcessingFactory,
                                  ObjectMapper objectMapper) {
        this.redisConnection = redisClient.connectPubSub();
        this.listenerProcessingFactory = listenerProcessingFactory;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        if (nonNull(redisConnection)) {
            redisConnection.addListener(listener);
            RedisClusterPubSubCommands<String, String> sync = redisConnection.sync();
            sync.subscribe(SSE_BROADCAST_TOPIC, WS_BROADCAST_TOPIC);
        }
    }

    @PreDestroy
    public void close() {
        if (redisConnection != null)
            redisConnection.close();
    }

    private class CustomRedisPubSubAdapter extends RedisClusterPubSubAdapter<String, String> {

        @Override
        public void message(RedisClusterNode node, String topic, String message) {
            log.info("RedisClusterSubscriber Topic : {} : Message {} ", topic, message);
            try {
                Message msg = objectMapper.readValue(message, Message.class);
                listenerProcessingFactory.getListenerProcessingService(topic).processMessage(msg);
            } catch (Exception e) {
                log.error("Error processing message from topic {}: {}", topic, e.getMessage(), e);
            }
        }

    }


}
