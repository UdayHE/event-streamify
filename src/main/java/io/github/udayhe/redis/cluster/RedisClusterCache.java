package io.github.udayhe.redis.cluster;

import io.github.udayhe.redis.RedisCache;
import io.lettuce.core.SetArgs;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author udayhegde
 */
@Singleton
@Slf4j
@Requires(property = "redis.enable", value = StringUtils.TRUE)
public class RedisClusterCache implements RedisCache {

    @Nullable
    private final StatefulRedisClusterConnection<String, String> redisConnection;


    public RedisClusterCache(RedisClusterClient redisClient) {
        this.redisConnection = redisClient.connect();
    }

    @Override
    public void set(String key, String value, Long ttl) {
        log.info("RedisClusterCache, setInCacheWithTimeOut key:{}, value:{}", key, value);
        if (nonNull(redisConnection))
            redisConnection.sync().set(key, value, new SetArgs().ex(ttl));

    }

    @Override
    public void set(String key, String value) {
        log.info("RedisClusterCache, setInCache key:{}, value:{}", key, value);
        if (nonNull(redisConnection))
            redisConnection.sync().set(key, value);

    }

    @Override
    public String getFromCache(String key) {
        log.info("RedisClusterCache, setInCacheWithTimeOut key:{}", key);
        return nonNull(redisConnection) ? redisConnection.sync().get(key) : EMPTY;
    }

    @Override
    public void removeKeyFromCache(String key) {
        log.info("RedisClusterCache,removeKeyFromCache  key:{}", key);
        if (nonNull(redisConnection))
            redisConnection.sync().del(key);
    }

    @Override
    public boolean keyPresentInCache(String key) {
        log.info("RedisClusterCache,keyPresentInCache  key:{}", key);
        Long value = null;
        if (nonNull(redisConnection)) {
            value = redisConnection.sync().exists(key);
            log.info("RedisClusterCache,keyPresentInCache  value:{}", value);
        }
        return Objects.equals(value, 1L);
    }

    @Override
    public void expireAfter(String key, Long ttl) {
        log.info("RedisClusterCache,expireAfter  key:{}", key);
        if (nonNull(redisConnection))
            redisConnection.sync().expire(key, ttl);
    }

    @Override
    public Long getDBSize() {
        return nonNull(redisConnection) ? redisConnection.sync().dbsize() : 0L;
    }

    @PreDestroy
    public void close() {
        if (redisConnection != null)
            redisConnection.close();
    }
}
