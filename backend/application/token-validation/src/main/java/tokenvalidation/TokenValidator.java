package tokenvalidation;


import exception.excrptions.TokenException;

public interface TokenValidator {
    /**
     * 토큰이 유효하지 않으면 TokenValidationException을 던진다.
     */
    void validate(String token)
            throws  TokenException;
}