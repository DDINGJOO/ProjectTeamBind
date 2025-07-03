package tokenvalidation;


import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ExpiryValidator extends AbstractTokenValidator {

    private final byte[] secretKey;

    public ExpiryValidator(
            @Value("${token.secret}") String secret,
            BlacklistValidator blacklistValidator  // 다음 체인으로 주입
    ) {
        super(blacklistValidator);
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doValidate(String token) throws TokenException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    // 필요시 약간의 시계 오차 허용
                    .setAllowedClockSkewSeconds(30)
                    .build()
                    .parseClaimsJws(token);
            // parseClaimsJws()가 예외 없이 리턴되면
            // • 시그니처 OK
            // • 만료 시점 OK
        } catch (TokenException e) {
            // 만료된 경우 TokenException 으로 바꿔 던져 줍니다
            throw new TokenException(TokenErrorCode.EXPIRED_TOKEN);
        }

    }
}