package io.github.udayhe.service;


import io.github.udayhe.request.Message;

public interface InteractiveSessionService extends ListenerProcessingService{

    String getTopic();
    void sendMessage(Message message);
    void processMessage(Message message);
    void updateChannelToSessionIdMap(String channel, String sessionId);
    void removeFromChannelToSessionIdMap(String channel);
}
