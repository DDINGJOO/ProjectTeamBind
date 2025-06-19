package auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private String getKey(String userId, String deviceId) {
        return "refresh:" + userId + ":" + deviceId;
    }

    public void save(String userId, String deviceId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue()
                .set(getKey(userId, deviceId), refreshToken, ttl);
    }

    public Optional<String> get(String userId, String deviceId) {
        String token = redisTemplate.opsForValue().get(getKey(userId, deviceId));
        return Optional.ofNullable(token);
    }

    public void delete(String userId, String deviceId) {
        redisTemplate.delete(getKey(userId, deviceId));
    }

    public void deleteAllByUser(String userId) {
        String pattern = "refresh:" + userId + ":*";
        redisTemplate.keys(pattern).forEach(redisTemplate::delete);
    }
}