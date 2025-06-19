package exception.error_code.token;

import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum TokenErrorCode implements CustomErrorCode {
    EXPIRED_TOKEN("TOKEN-EXP-001", "마감/ 파기된 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("TOKEN-VAR-001", "잘못된 토큰 입니다. ", HttpStatus.BAD_REQUEST ),
    TOKEN_CREATION_FAILED("TOKEN-CRE-001", "토큰 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    TokenErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }

    @Override
    public HttpStatus getStatus() { return status; }
}
