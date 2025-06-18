package exception.error_code.auth;

import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements CustomErrorCode {

    DUPLICATE_EMAIL("AUTH-REG-001", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("AUTH-VAL-001", "비밀번호가 정책에 부합하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("AUTH-VAL-002", "이메일 정책에 부합하지 않습니다.", HttpStatus.BAD_REQUEST ),
    USER_NOT_FOUND("AUTH-VAL-003", "유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    AuthErrorCode(String code, String message, HttpStatus status) {
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
