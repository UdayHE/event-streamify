package io.github.udayhe.service;


import io.github.udayhe.request.Message;

/**
 * @author udayhegde
 */
public interface ListenerProcessingService {

    String getTopic();

    void processMessage(Message message);
}
