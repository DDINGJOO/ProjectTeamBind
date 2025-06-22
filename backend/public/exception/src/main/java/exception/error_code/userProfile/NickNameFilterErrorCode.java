package exception.error_code.userProfile;


import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum NickNameFilterErrorCode implements CustomErrorCode {

    NICKNAME_EMPTY("NICK-400-001", "닉네임이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_TOO_SHORT("NICK-400-002", "닉네임은 최소 2자 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_TOO_LONG("NICK-400-003", "닉네임은 최대 20자 이하여야 합니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_CONTAINS_SPACES("NICK-400-004", "닉네임에 공백을 포함할 수 없습니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_INVALID_FORMAT("NICK-400-006", "닉네임 형식이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    NickNameFilterErrorCode(String code, String message, HttpStatus status) {
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