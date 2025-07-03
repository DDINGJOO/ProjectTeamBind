package tokenvalidation;


import exception.excrptions.TokenException;

public abstract class AbstractTokenValidator implements TokenValidator {

    private final TokenValidator next;

    protected AbstractTokenValidator(TokenValidator next) {
        this.next = next;
    }

    @Override
    public void validate(String token) throws TokenException {
        // 1) 현재 단계 검증
        doValidate(token);
        // 2) 다음 단계 검증
        if (next != null) {
            next.validate(token);
        }
    }

    protected abstract void doValidate(String token) throws TokenException;
}