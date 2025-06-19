package token;


import exception.excrptions.TokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "mysecretkeymysecretkeymysecretkeymysecretkey"; // 최소 32자
        long accessExpiration = 3600; // 1시간
        long refreshExpiration = 86400; // 1일
        tokenProvider = new JwtTokenProvider(secret, accessExpiration, refreshExpiration);
    }

    @Test
    void 토큰_정상_생성_및_파싱() {
        // given
        String subject = "user-123";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        // when
        String token = tokenProvider.issueAccessToken(subject, claims);
        Claims parsed = tokenProvider.parse(token);

        // then
        assertThat(parsed.getSubject()).isEqualTo(subject);
        assertThat(parsed.get("role")).isEqualTo("USER");
    }

    @Test
    void 유효한_토큰은_parse_성공() {
        String token = tokenProvider.issueAccessToken("user-456", Collections.emptyMap());

        Claims claims = tokenProvider.parse(token);
        assertThat(claims.getSubject()).isEqualTo("user-456");
    }

    @Test
    void 위조된_토큰은_InvalidTokenException_발생() {
        String token = tokenProvider.issueAccessToken("user-789", Collections.emptyMap());
        String tampered = token + "tamper";

        assertThatThrownBy(() -> tokenProvider.parse(tampered))
                .isInstanceOf(TokenException.class);
    }

    @Test
    void 토큰에서_subject_추출() {
        String token = tokenProvider.issueAccessToken("user-999", Collections.emptyMap());

        String subject = tokenProvider.getClaim(token, "sub");
        assertThat(subject).isEqualTo("user-999");
    }
}