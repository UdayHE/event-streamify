package io.github.udayhe.request;

import io.micronaut.serde.annotation.Serdeable;
import lombok.*;

/**
 * @author udayhegde
 */
@Setter
@Getter
@Serdeable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelInfo {

    private String channelName;
}
