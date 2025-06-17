package token;


import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "mysecretkeymysecretkeymysecretkey"; // 최소 256bit (32자리 이상)
        long expiration = 3600; // 1시간
        tokenProvider = new JwtTokenProvider(secret, expiration);
    }

    @Test
    void 토큰_정상_생성_및_파싱() {
        // given
        String subject = "user-123";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        // when
        String token = tokenProvider.issueToken(subject, claims);
        Claims parsed = tokenProvider.parse(token);

        // then
        assertThat(parsed.getSubject()).isEqualTo(subject);
        assertThat(parsed.get("role")).isEqualTo("USER");
    }

    @Test
    void 토큰_유효성_검사() {
        String token = tokenProvider.issueToken("user-456", Collections.emptyMap());

        assertThat(tokenProvider.isValid(token)).isTrue();
        assertThat(tokenProvider.isValid(token + "tampered")).isFalse(); // 위조된 토큰
    }

    @Test
    void 토큰에서_subject_추출() {
        String token = tokenProvider.issueToken("user-789", Collections.emptyMap());

        String subject = tokenProvider.getSubject(token);
        assertThat(subject).isEqualTo("user-789");
    }
}