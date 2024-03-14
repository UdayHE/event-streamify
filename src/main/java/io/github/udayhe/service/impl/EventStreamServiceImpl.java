package io.github.udayhe.service.impl;

import io.github.udayhe.helper.InstanceIdentificationHelper;
import io.github.udayhe.redis.RedisPublisher;
import io.github.udayhe.request.ChannelInfo;
import io.github.udayhe.request.Message;
import io.github.udayhe.response.ChannelStatusResponse;
import io.github.udayhe.service.CacheService;
import io.github.udayhe.service.EventStreamService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.udayhe.util.Constants.*;
import static io.github.udayhe.util.NullSafeUtils.nullSafeGet;
import static java.lang.System.currentTimeMillis;
import static java.time.Instant.ofEpochMilli;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class EventStreamServiceImpl implements EventStreamService {
    private final Map<String, Pair<PublishProcessor<Message>, AtomicInteger>> channelProcessorMap = new ConcurrentHashMap<>();
    @Nullable
    private final RedisPublisher redisPublisher;
    private final InstanceIdentificationHelper instanceIdentificationHelper;
    private final CacheService<ChannelInfo> cacheService;


    @Override
    public String getTopic() {
        return SSE_BROADCAST_TOPIC;
    }

    public Flowable<Message> register(String channel) {
        cacheService.set(channel, new ChannelInfo(), REDIS_TTL);
        Pair<PublishProcessor<Message>, AtomicInteger> pair = channelProcessorMap.computeIfAbsent(channel, c ->
                Pair.of(PublishProcessor.create(), new AtomicInteger(0)));
        PublishProcessor<Message> processor = pair.getLeft();
        AtomicInteger subscriberCount = pair.getRight();
        return processor.startWithItem(Message.builder()
                        .registeredServerID(instanceIdentificationHelper.getIdentifier())
                        .isPing(true).build())
                .doOnSubscribe(subscription -> {
                    subscriberCount.incrementAndGet();
                    log.info("Action : doOnSubscribe  Channel : {} Count : {}  ", channel, subscriberCount.get());
                })
                .doAfterNext(message -> log.info("After receiving next item: {} on channel: {}", message, channel))
                .doFinally(() -> {
                    if (subscriberCount.decrementAndGet() == 0) {
                        channelProcessorMap.remove(channel);
                        processor.onComplete();
                    }
                    cacheService.removeKeyFromCache(channel);
                    log.info("Action : doFinally  Channel : {} Count : {}  ", channel, subscriberCount.get());
                });
    }

    @Override
    public void emitEvent(String channel, Map<String, Object> payload) {
        try {
            Message message = Message.builder()
                    .sourceServerID(instanceIdentificationHelper.getIdentifier())
                    .channel(channel).payload(payload).build();
            if (nonNull(redisPublisher))
                redisPublisher.publishMessage(SSE_BROADCAST_TOPIC, message);
            else
                processMessage(message);
        } catch (Exception e) {
            log.error("Exception in emitEvent.", e);
        }
    }

    @Override
    public void processMessage(Message message) {
        String channel = message.getChannel();
        if (isTrue(channelConnected(channel))) {
            Pair<PublishProcessor<Message>, AtomicInteger> pair = channelProcessorMap.get(channel);
            PublishProcessor<Message> processor = pair.getLeft();
            log.info("In SSE publish has Subscribers...");
            message.setDestinationServerID(instanceIdentificationHelper.getIdentifier());
            processor.onNext(message);
        } else {
            log.info("In SSE publish has no Subscribers...");
        }
    }

    @Override
    public ChannelStatusResponse channelStatus(String channel) {
        return ChannelStatusResponse
                .builder()
                .status(isTrue(channelConnected(channel)) ? CONNECTED : NOT_CONNECTED)
                .serverID(instanceIdentificationHelper.getIdentifier())
                .build();
    }

    @Override
    public void heartBeat(String channel) {
        ChannelInfo channelInfo = cacheService.getFromCache(channel);
        cacheService.set(channel, channelInfo, REDIS_TTL);
    }

    @Override
    public ChannelStatusResponse channelStatusV2(String channel) {
        return ChannelStatusResponse
                .builder()
                .status(isTrue(cacheService.keyPresentInCache(channel)) ? CONNECTED : NOT_CONNECTED)
                .serverID(instanceIdentificationHelper.getIdentifier())
                .build();
    }

    @Override
    public void updateCacheFromChannelProcessorMap() {
        long startTime = currentTimeMillis();
        log.info("In updateCacheFromChannelProcessorMap:{}, Time:{}", getCount(), ofEpochMilli(startTime));
        for (String key : nullSafeGet(channelProcessorMap, Map::keySet, new HashSet<String>())) {
            if (isNotBlank(key)) {
                //If key->value not found in cache then set new ChannelInfo() for the key
                ChannelInfo channelInfo = nullSafeGet(cacheService, cache -> cache.getFromCache(key), new ChannelInfo());
                cacheService.set(key, channelInfo, REDIS_TTL);
            }
        }
        log.info("In updateCacheFromChannelProcessorMap, endTime:{}", (currentTimeMillis() - startTime));
    }

    @Override
    public Map<String, Number> getCount() {
        Map<String, Number> output = new HashMap<>();
        output.put("inMemoryCount", channelProcessorMap.keySet().size());
        output.put("redisCount", cacheService.getCacheDBSize());
        return output;
    }

    private Boolean channelConnected(String channel) {
        return channelProcessorMap.containsKey(channel)
                && channelProcessorMap.get(channel).getLeft().hasSubscribers();
    }

}
