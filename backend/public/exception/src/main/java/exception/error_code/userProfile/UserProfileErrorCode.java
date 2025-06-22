package exception.error_code.userProfile;

import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum UserProfileErrorCode implements CustomErrorCode {

    USER_PROFILE_NOT_FOUND("UPF-404-001", "해당 유저 프로필이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    USER_PROFILE_ALREADY_EXISTS("UPF-409-001", "이미 유저 프로필이 존재합니다.", HttpStatus.CONFLICT),
    USER_PROFILE_ALREADY_DELETED("UPF-400-001", "이미 삭제된 유저입니다.", HttpStatus.BAD_REQUEST),
    USER_PROFILE_ACCESS_DENIED("UPF-403-001", "해당 유저 프로필에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserProfileErrorCode(String code, String message, HttpStatus status) {
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