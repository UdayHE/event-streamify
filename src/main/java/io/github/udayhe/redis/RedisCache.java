package io.github.udayhe.redis;

/**
 * @author udayhegde
 */
public interface RedisCache {

    void set(String key, String value, Long ttl);

    void set(String key, String value);

    String getFromCache(String key);

    void removeKeyFromCache(String key);

    boolean keyPresentInCache(String key);

    void expireAfter(String key, Long ttl);

    Long getDBSize();
}
