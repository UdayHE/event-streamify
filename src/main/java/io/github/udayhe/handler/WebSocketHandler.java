package io.github.udayhe.handler;

/**
 * @author udayhegde
 */

import io.github.udayhe.service.InteractiveSessionService;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ServerWebSocket("/ws/{channel}")
public class WebSocketHandler {

    private final InteractiveSessionService interactiveSessionService;

    @OnOpen
    public void onOpen(String channel, WebSocketSession session) {
        log.info("onOpen, {}, {}", session, channel);
        interactiveSessionService.updateChannelToSessionIdMap(channel, session.getId());
    }

    @OnMessage
    public void onMessage(String channel,
                          String message,
                          WebSocketSession session) {
        log.info("onMessage, {}, {}, {}", session, channel, message);
    }

    @OnError
    public void onError(String channel, WebSocketSession session) {
        log.info("onError, {}, {}", session, channel);
    }

    @OnClose
    public void onClose(String channel, WebSocketSession session) {
        log.info("onClose, {}, {}", session, channel);
        interactiveSessionService.removeFromChannelToSessionIdMap(channel);
    }

}
