package io.github.udayhe.redis;


import io.github.udayhe.request.Message;

import java.io.IOException;

/**
 * @author udayhegde
 */
public interface RedisPublisher {

    void publishMessage(String topic, Message message) throws IOException;
}
