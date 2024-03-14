package io.github.udayhe.service;

/**
 * @author udayhegde
 */
public interface CacheService<T> {

    void set(String key, T value, long ttlInSeconds);

    void set(String key, T value);

    T getFromCache(String key);

    boolean keyPresentInCache(String key);

    void removeKeyFromCache(String key);

    void expireAfter(String key, Long ttl);

    Long getCacheDBSize();


}
