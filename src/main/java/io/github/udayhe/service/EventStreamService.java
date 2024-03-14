package io.github.udayhe.service;


import io.github.udayhe.request.Message;
import io.github.udayhe.response.ChannelStatusResponse;
import io.reactivex.rxjava3.core.Flowable;

import java.util.Map;

public interface EventStreamService extends ListenerProcessingService{

     String getTopic();
     Flowable<Message> register(String channel);
     void emitEvent(String channel, Map<String, Object> payload);
     void processMessage(Message message);
     ChannelStatusResponse channelStatus(String channel);
     void heartBeat(String channel);
     ChannelStatusResponse channelStatusV2(String channel);
     void updateCacheFromChannelProcessorMap();
     Map<String, Number> getCount();
}
