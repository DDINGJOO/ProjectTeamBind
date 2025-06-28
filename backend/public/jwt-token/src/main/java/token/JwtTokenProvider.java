package token;

import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;

    public JwtTokenProvider(String secret, long accessExp, long refreshExp) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessExp;
        this.refreshTokenExpirationSeconds = refreshExp;
    }

    public String issueAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, accessTokenExpirationSeconds);
    }

    public String issueRefreshToken(String subject, String deviceId) {
        return buildToken(subject, Map.of("deviceId", deviceId), refreshTokenExpirationSeconds);
    }

    private String buildToken(String subject, Map<String, Object> claims, long ttlSec) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlSec * 1000);
        String jti = UUID.randomUUID().toString();
        try {
            return Jwts.builder()
                    .setSubject(subject)
                    .addClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .setId(jti)
                    .signWith(key)
                    .compact();
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenException(TokenErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    public Claims parse(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }
    }

    public String getJti(String token) {
        return parse(token).getId();
    }

    public Date getExpiration(String token) {
        return parse(token).getExpiration();
    }

    public Duration computeTTL(String token) {
        long diff = getExpiration(token).getTime() - System.currentTimeMillis();
        return diff > 0 ? Duration.ofMillis(diff) : Duration.ZERO;
    }

    public Duration getAccessTokenTTL() {
        return Duration.ofSeconds(accessTokenExpirationSeconds);
    }

    public Duration getRefreshTokenTTL() {
        return Duration.ofSeconds(refreshTokenExpirationSeconds);
    }

    /**
     * 토큰의 클레임을 원하는 타입으로 꺼내기
     *
     * @param token    JWT 문자열
     * @param claimKey 클레임 이름
     * @param clazz    반환받을 타입 클래스
     * @param <T>      반환 타입
     * @return 클레임 값
     */
    public <T> T getClaim(String token, String claimKey, Class<T> clazz) {
        return parse(token).get(claimKey, clazz);
    }

    /**
     * 토큰의 클레임을 String으로 꺼내기 (간편 호출용)
     *
     * @param token    JWT 문자열
     * @param claimKey 클레임 이름
     * @return 클레임 값 (String)
     */
    public String getClaim(String token, String claimKey) {
        return getClaim(token, claimKey, String.class);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parse(token);                    // 서명/만료까지 검증
        String userId = claims.getSubject();             // sub
        String role = claims.get("role", String.class);// role 클레임

        // Spring Security 권한으로 변환 (ROLE_ 접두어는 관습)
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + role);

        // principal 에 userId 혹은 커스텀 객체를 넣어도 됩니다.
        return new UsernamePasswordAuthenticationToken(
                userId,      // principal
                null,        // credentials (이미 JWT 임)
                List.of(authority)
        );
    }
}