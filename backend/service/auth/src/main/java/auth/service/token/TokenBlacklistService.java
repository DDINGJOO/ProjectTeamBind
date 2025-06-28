package auth.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import token.JwtTokenProvider;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private static final String PREFIX = "blacklist:jwt:";
    private final StringRedisTemplate redis;
    private final JwtTokenProvider jwtTokenProvider;

    /** 한 토큰만 로그아웃(블랙리스트)에 올리기 */
    public void revokeToken(String token) {
        String jti = jwtTokenProvider.getJti(token);
        // 만료시간까지 유효하도록 TTL 계산
        Duration ttl = Duration.between(
                Instant.now(),
                jwtTokenProvider.getExpiration(token).toInstant()
        );
        redis.opsForValue()
                .set(PREFIX + jti, "1", ttl);
    }

    /** userId+deviceId 단위의 모든 리프레시 토큰 삭제는 AuthService에 맡기고,
     AccessToken은 위 revokeToken() 하나만 하면 됩니다. */
    public boolean isBlacklisted(String token) {
        String jti = jwtTokenProvider.getJti(token);
        return redis.hasKey(PREFIX + jti);
    }
}