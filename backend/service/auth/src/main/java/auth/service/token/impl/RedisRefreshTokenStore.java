package auth.service.token.impl;


import auth.service.token.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {
    private static final String PREFIX = "refresh:";
    private final StringRedisTemplate redis;

    private String key(String u, String d) {
        return PREFIX + u + ":" + d;
    }

    @Override
    public void save(String userId, String deviceId, String token, Duration ttl) {
        redis.opsForValue().set(key(userId, deviceId), token, ttl);
    }

    @Override
    public Optional<String> find(String userId, String deviceId) {
        return Optional.ofNullable(redis.opsForValue().get(key(userId, deviceId)));
    }

    @Override
    public void remove(String userId, String deviceId) {
        redis.delete(key(userId, deviceId));
    }

    @Override
    public void removeAll(String userId) {
        String pattern = PREFIX + userId + ":*";
        try (var conn = redis.getConnectionFactory().getConnection()) {
            var opts = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(1000)
                    .build();
            try (Cursor<byte[]> cur = conn.scan(opts)) {
                cur.forEachRemaining(k -> redis.delete(new String(k, StandardCharsets.UTF_8)));
            }
        }
    }

    @Override
    public boolean isValid(String userId, String deviceId, String token) {
        return find(userId, deviceId).filter(token::equals).isPresent();
    }
}