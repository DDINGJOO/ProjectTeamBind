package exception.error_code;

import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;
/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
public enum GlobalErrorCode implements CustomErrorCode {

    INVALID_INPUT("COMMON-001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("COMMON-999", "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    GlobalErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
