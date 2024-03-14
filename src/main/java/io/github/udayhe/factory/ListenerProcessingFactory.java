package io.github.udayhe.factory;

import io.github.udayhe.service.ListenerProcessingService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * @author udayhegde
 */
@Singleton
@Slf4j
public class ListenerProcessingFactory {

    private final Map<String, ListenerProcessingService> topicToListener;

    @Inject
    ListenerProcessingFactory(List<ListenerProcessingService> listenerProcessingServices) {
        Map<String, ListenerProcessingService> tempTopicToListener = new HashMap<>();
        listenerProcessingServices.forEach(listenerProcessingService -> {
            try {
                tempTopicToListener.put(
                        listenerProcessingService.getTopic(),
                        listenerProcessingService);
            } catch (Exception e) {
                log.error("Error initializing ListenerProcessingFactory", e);
            }
        });
        synchronized (ListenerProcessingFactory.class) {
            topicToListener = unmodifiableMap(tempTopicToListener);
        }
    }

    public ListenerProcessingService getListenerProcessingService(String topic) {
        return topicToListener.get(topic);
    }

}
