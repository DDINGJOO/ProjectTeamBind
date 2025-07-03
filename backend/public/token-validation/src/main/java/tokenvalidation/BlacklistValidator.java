package tokenvalidation;


import exception.error_code.token.TokenErrorCode;
import exception.excrptions.TokenException;
import org.springframework.stereotype.Component;

@Component
public class BlacklistValidator extends AbstractTokenValidator {

    private final TokenBlacklistService blacklistService;

    public BlacklistValidator(TokenBlacklistService blacklistService) {
        super(null);  // 체인의 마지막
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doValidate(String token) throws TokenException {
        if (blacklistService.isBlacklisted(token)) {
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }
    }
}
