package io.github.udayhe.util;

import java.util.UUID;

public class Constants {
    public static final String SSE_BROADCAST_TOPIC = "sse-broadcast";
    public static final String WS_BROADCAST_TOPIC = "ws-broadcast";
    public static final String CONNECTED = "Connected";
    public static final String NOT_CONNECTED = "Not-Connected";
    public static final String HOSTNAME_ENV = "HOSTNAME";
    public static final String SCHEDULER_DELAY = "4s";
    public static final Long REDIS_TTL = 5L;
    public static final String UID = UUID.randomUUID().toString();
}
