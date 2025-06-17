package exception.error_code;

import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;
/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
public enum UserErrorCode implements CustomErrorCode {
    USER_NOT_FOUND("USER-001", "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("USER-002", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("USER-003", "비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserErrorCode(String code, String message, HttpStatus status) {
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