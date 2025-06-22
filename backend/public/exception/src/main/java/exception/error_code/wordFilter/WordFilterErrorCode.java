package exception.error_code.wordFilter;

import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum WordFilterErrorCode implements CustomErrorCode {
    BAD_WORD_DETECTED("WDF-400-001", "부적절한 단어가 포함되어 있습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    WordFilterErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public HttpStatus getStatus() { return status; }
}