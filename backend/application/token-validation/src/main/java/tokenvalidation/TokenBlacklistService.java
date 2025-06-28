package tokenvalidation;

import exception.excrptions.TokenException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import token.JwtTokenProvider;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String PREFIX = "blacklist:";

    private final StringRedisTemplate redis;
    private final JwtTokenProvider jwtTokenProvider;  // ← provider 주입

    /** 토큰 블랙리스트 여부 조회 */
    public boolean isBlacklisted(String token) {
        try {
            // JwtTokenProvider.parse() 로 서명 검증 포함 파싱
            Claims claims = jwtTokenProvider.parse(token);
            String jti = claims.getId();
            return Boolean.TRUE.equals(redis.hasKey(PREFIX + jti));
        } catch (TokenException e) {
            // 만료·잘못된 토큰도 블랙리스트 처리
            return true;
        }
    }

    /** 블랙리스트에 토큰 추가 */
    public void blacklist(String token) {
        Claims claims = jwtTokenProvider.parse(token);
        String jti = claims.getId();
        // 실제 남은 TTL 계산
        Duration ttl = jwtTokenProvider.computeTTL(token);
        redis.opsForValue().set(PREFIX + jti, "1", ttl);
    }
}