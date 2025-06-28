package auth.service.token.impl;

import auth.service.token.TokenBlacklistStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import token.JwtTokenProvider;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenBlacklistStore implements TokenBlacklistStore {
    private static final String PREFIX = "blacklist:";
    private final StringRedisTemplate redis;
    private final JwtTokenProvider provider;

    @Override
    public void blacklist(String token) {
        String jti = provider.getJti(token);
        Duration ttl = provider.computeTTL(token);
        redis.opsForValue().set(PREFIX + jti, "1", ttl);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String jti = provider.getJti(token);
        return redis.hasKey(PREFIX + jti);
    }
}