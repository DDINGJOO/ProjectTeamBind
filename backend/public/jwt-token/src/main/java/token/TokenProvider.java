package token;


import io.jsonwebtoken.Claims;

import java.time.Duration;
import java.util.Map;

public interface TokenProvider {
    String issueToken(String subject, Map<String, Object> claims);
    Claims parse(String token);
    String getJti(String token);
    String getClaim(String token, String claimKey);
    Duration getTokenTTL();
}