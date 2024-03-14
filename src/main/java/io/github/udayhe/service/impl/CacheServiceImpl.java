package io.github.udayhe.service.impl;

import io.github.udayhe.redis.RedisCache;
import io.github.udayhe.request.ChannelInfo;
import io.github.udayhe.service.CacheService;
import io.micronaut.serde.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

/**
 * @author udayhegde
 */
@Slf4j
@Singleton
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    @Nullable
    private final RedisCache redisCache;
    private final ObjectMapper objectMapper;

    @Override
    public void set(String key, Object value, long ttlInSeconds) {
        try {
            if (nonNull(redisCache)) {
                redisCache.set(key,
                        objectMapper.writeValueAsString(value), ttlInSeconds);
            }
        } catch (Exception e) {
            log.error("Exception in setInCache.", e);
        }
    }

    @Override
    public void set(String key, Object value) {
        try {
            if (nonNull(redisCache)) {
                redisCache.set(key, objectMapper.writeValueAsString(value));
            }
        } catch (Exception e) {
            log.error("Exception in setInCache.", e);
        }
    }

    public Object getFromCache(String key) {
        ChannelInfo channelInfo = null;
        try {
            if (nonNull(redisCache)) {
                String value = redisCache.getFromCache(key);
                channelInfo = objectMapper.readValue(value, ChannelInfo.class);
            }
        } catch (Exception e) {
            log.error("Exception in updateChannelInfoInCache.", e);
        }
        return channelInfo;
    }

    @Override
    public boolean keyPresentInCache(String key) {
        try {
            if (nonNull(redisCache))
                return redisCache.keyPresentInCache(key);
        } catch (Exception e) {
            log.error("Exception in keyPresentInCache.", e);
        }
        log.info("keyPresentInCache, returning false");
        return false;
    }

    @Override
    public void removeKeyFromCache(String key) {
        try {
            if (nonNull(redisCache))
                redisCache.removeKeyFromCache(key);
        } catch (Exception e) {
            log.error("Exception in removeKeyFromCache.", e);
        }
    }

    @Override
    public void expireAfter(String key, Long ttl) {
        try {
            if (nonNull(redisCache))
                redisCache.expireAfter(key, ttl);
        } catch (Exception e) {
            log.error("Exception in expireAfter.", e);
        }
    }

    @Override
    public Long getCacheDBSize() {
        try {
            if (nonNull(redisCache))
                return redisCache.getDBSize();
        } catch (Exception e) {
            log.error("Exception in getCacheDBSize.", e);
        }
        return 0L;
    }

}
