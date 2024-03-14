package io.github.udayhe.response;

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
public class ChannelStatusResponse {

    private String status;
    private String serverID;
}
