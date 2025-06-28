package tokenvalidation;

import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Primary
public class SignatureValidator extends AbstractTokenValidator {

    private final byte[] secretKey;

    public SignatureValidator(
            @Value("${token.secret}") String secret,
            ExpiryValidator expiryValidator  // 다음 체인으로 주입
    ) {
        super(expiryValidator);
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doValidate(String token) throws TokenException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey))
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }
    }
}