package exception.error_code.bff;


import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum BffErrorCode implements CustomErrorCode {
    NOT_MATCHED_TOKEN("BFF-400-001", "토큰 정보와 맞지 않습니다." , HttpStatus.BAD_REQUEST ),
    EMPTY_RESPONSE("BFF-400-002", "요청 결과가 비어있습니다.." , HttpStatus.BAD_REQUEST ),
    SERVER_ERROR("BFF-500-001","서버에러" , HttpStatus.BAD_REQUEST);
    private final String code;
    private final String message;
    private final HttpStatus status;

    BffErrorCode(String code, String message, HttpStatus status) {
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
