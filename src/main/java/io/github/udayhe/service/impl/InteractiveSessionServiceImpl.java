package io.github.udayhe.service.impl;


import io.github.udayhe.helper.InstanceIdentificationHelper;
import io.github.udayhe.redis.RedisPublisher;
import io.github.udayhe.request.Message;
import io.github.udayhe.service.InteractiveSessionService;
import io.micronaut.websocket.WebSocketBroadcaster;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static io.github.udayhe.util.Constants.WS_BROADCAST_TOPIC;
import static io.github.udayhe.util.NullSafeUtils.nullSafeGet;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class InteractiveSessionServiceImpl implements InteractiveSessionService {

    private final Map<String, String> channelToSessionId = new ConcurrentHashMap<>();

    private final WebSocketBroadcaster webSocketBroadcaster;
    @Nullable
    private final RedisPublisher redisPublisher;
    private final InstanceIdentificationHelper instanceIdentificationHelper;

    @Override
    public String getTopic() {
        return WS_BROADCAST_TOPIC;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            if (nonNull(redisPublisher))
                redisPublisher.publishMessage(WS_BROADCAST_TOPIC, message);
            else
                processMessage(message);
        } catch (Exception e) {
            log.error("Exception in InteractiveSessionServiceImpl, sendMessage:", e);
        }
    }

    @Override
    public void processMessage(Message message) {
        String channel = nullSafeGet(message, Message::getChannel, EMPTY);
        String sessionId = channelToSessionId.get(channel);
        message.setDestinationServerID(instanceIdentificationHelper.getIdentifier());
        if (isNotBlank(sessionId))
            webSocketBroadcaster.broadcastSync(message,
                    session -> sessionId.equals(session.getId()));
    }

    @Override
    public void updateChannelToSessionIdMap(String channel, String sessionId) {
        channelToSessionId.put(channel, sessionId);
    }

    @Override
    public void removeFromChannelToSessionIdMap(String channel) {
        channelToSessionId.remove(channel);
    }
}
