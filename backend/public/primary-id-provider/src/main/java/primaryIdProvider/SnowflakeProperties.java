package primaryIdProvider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {
    private long nodeId;

}
