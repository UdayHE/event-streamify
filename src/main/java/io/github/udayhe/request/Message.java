package io.github.udayhe.request;

import io.micronaut.serde.annotation.Serdeable;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Serdeable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Builder.Default
    private Map<String, Object> payload = new HashMap<>();
    private String channel;
    private String registeredServerID;
    private String sourceServerID;
    private String destinationServerID;
    private  boolean isPing;
}

