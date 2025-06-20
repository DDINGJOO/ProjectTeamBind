package auth.service;


import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import token.JwtTokenProvider;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public void logout(String accessToken, String userId, String deviceId) {
        refreshTokenService.delete(userId, deviceId);

        try {
            String jti = jwtTokenProvider.getJti(accessToken);
            Duration ttl = jwtTokenProvider.getAccessTokenTTL(); // 또는 위에서 말한 실시간 TTL

            redisTemplate.opsForValue()
                    .set("blacklist:" + jti, "1", ttl);
        } catch (TokenException e) {
            // 로그아웃 실패 로깅
            log.warn("로그아웃 중 토큰 처리 실패 userId={}, reason={}", userId, e.getMessage());
            throw  new TokenException(TokenErrorCode.INVALID_TOKEN);
        }
    }


    public void logoutAll(String accessToken, String userId) {
        refreshTokenService.deleteAllByUser(userId);

        String jti = jwtTokenProvider.getJti(accessToken);
        Duration ttl = jwtTokenProvider.getAccessTokenTTL();

        redisTemplate.opsForValue()
                .set("blacklist:" + jti, "1", ttl);
    }



    public boolean isBlacklisted(String jti) {
        return redisTemplate.hasKey("blacklist:" + jti);
    }
}