package token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long accessExpiration;   // ex) 15 * 60
    private long refreshExpiration;  // ex) 14 * 24 * 60 * 60

}