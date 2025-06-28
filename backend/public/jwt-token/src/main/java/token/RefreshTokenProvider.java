package token;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RefreshTokenProvider implements TokenProvider {
    private final JwtTokenProvider core;
    @Override
    public String issueToken(String subject, Map<String, Object> claims) {
        Object raw = claims.get("deviceId");
        if (raw == null) {
            throw new IllegalArgumentException("RefreshTokenProvider: missing required claim 'deviceId'");
        }
        String deviceId = raw.toString().trim();
        if (deviceId.isEmpty()) {
            throw new IllegalArgumentException("RefreshTokenProvider: 'deviceId' cannot be empty");
        }
        return core.issueRefreshToken(subject, deviceId);
    }

    @Override public Claims parse(String token) { return core.parse(token); }
    @Override public String getJti(String token)  { return core.getJti(token); }
    @Override public String getClaim(String token, String key) { return core.getClaim(token, key); }
    @Override public Duration getTokenTTL() { return core.getRefreshTokenTTL(); }
}