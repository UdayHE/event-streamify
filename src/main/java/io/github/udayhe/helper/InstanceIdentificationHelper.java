package io.github.udayhe.helper;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static io.github.udayhe.util.Constants.HOSTNAME_ENV;
import static io.github.udayhe.util.Constants.UID;

/**
 * @author udayhegde
 */
@Singleton
@Slf4j
public class InstanceIdentificationHelper {


    public String getIdentifier() {
        String hostName = System.getenv(HOSTNAME_ENV);
        hostName = StringUtils.isNotBlank(hostName) ? hostName : UID;
        log.info("hostname : {} ", hostName);
        return hostName;
    }

}
