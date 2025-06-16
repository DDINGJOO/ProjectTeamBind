package primaryIdProvider;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeConfig {

    @Bean
    public Snowflake snowflake(SnowflakeProperties properties) {
        return new Snowflake(properties.getNodeId());
    }
}
