package exception.error_code.auth;

import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements CustomErrorCode {

    DUPLICATE_EMAIL("AUTH-REG-001", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("AUTH-VAL-001", "비밀번호가 정책에 부합하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("AUTH-VAL-002", "이메일 정책에 부합하지 않습니다.", HttpStatus.BAD_REQUEST ),
    USER_NOT_FOUND("AUTH-VAL-003", "유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PASSWORD_CONFIRM_MISMATCH("AUTH-VAL-004", "비밀번호와 비밀번호 확인이 다릅니다.", HttpStatus.BAD_REQUEST ),
    UNAUTHORIZED_ROLE_ASSIGNMENT("AUTH-005", "해당 권한을 부여할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    USER_ROLE_NOT_FOUND("AUTH-006", "유저의 권한 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    IS_ALREADY_CONFIRMED("AUTH-VAL-007", "이미 이메일이 인증된 유저입니다.", HttpStatus.CONFLICT),
    EMAIL_NOT_CONFIRMD("AUTH-VAL-008", "이메일 인증이 되지 않은 유저입니다.", HttpStatus.BAD_REQUEST ),
    INVALID_USER("AUTH-VAL-009", "활동이 정지된 유저입니다.", HttpStatus.BAD_REQUEST ),
    DELETED_USER("AUTH-VAL-010", "삭제 대기중인 유저 입니다.", HttpStatus.BAD_REQUEST ),
    ;


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
