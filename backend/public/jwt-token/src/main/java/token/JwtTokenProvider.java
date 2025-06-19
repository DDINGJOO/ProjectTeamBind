package token;

import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

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
        try {
            return buildToken(subject, claims, accessTokenExpirationSeconds);
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenException(TokenErrorCode.TOKEN_CREATION_FAILED);
        }
    }


    public String issueRefreshToken(String subject, String deviceId) {
        return buildToken(subject, Map.of("deviceId", deviceId), refreshTokenExpirationSeconds);
    }

    private String buildToken(String subject, Map<String, Object> claims, long ttlSec) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlSec * 1000);

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }
    }

    public String getClaim(String token, String claimKey) {
        return parse(token).get(claimKey, String.class);
    }

    public Duration getAccessTokenTTL() {
        return Duration.ofSeconds(accessTokenExpirationSeconds);
    }

    public Duration getRefreshTokenTTL() {
        return Duration.ofSeconds(refreshTokenExpirationSeconds);
    }
}