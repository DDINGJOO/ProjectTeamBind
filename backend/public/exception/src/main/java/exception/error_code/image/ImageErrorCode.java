package exception.error_code.image;


import exception.CustomErrorCode;
import org.springframework.http.HttpStatus;

public enum ImageErrorCode implements CustomErrorCode {

    // 400 BAD REQUEST
    INVALID_IMAGE_FORMAT("IMG-400-001", "잘못된 이미지 형식입니다.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_CATEGORY("IMG-400-002", "이미지 카테고리가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 403 FORBIDDEN
    UNAUTHORIZED_ACCESS("IMG-403-001", "해당 이미지에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),

    // 404 NOT FOUND
    IMAGE_NOT_FOUND("IMG-404-001", "이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    IMAGE_NOT_TEMP("IMG-404-002", "임시 이미지가 아닙니다.", HttpStatus.NOT_FOUND),

    // 409 CONFLICT
    IMAGE_ALREADY_EXISTS("IMG-409-001", "이미지가 이미 존재합니다.", HttpStatus.CONFLICT),
    IMAGE_ALREADY_PENDING_DELETE("IMG-409-002", "이미 삭제 예정 상태입니다.", HttpStatus.CONFLICT),

    // 413 PAYLOAD TOO LARGE
    IMAGE_TOO_LARGE("IMG-413-001", "이미지 용량이 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE),

    // 500 INTERNAL SERVER ERROR
    IMAGE_UPLOAD_FAILED("IMG-500-001", "이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_DELETION_FAILED("IMG-500-002", "이미지 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_UPDATE_FAILED("IMG-500-003", "이미지 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_CONFIRMATION_FAILED("IMG-500-004", "이미지 확정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_DOWNLOAD_FAILED("IMG-500-005", "이미지 다운로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_RETRIEVAL_FAILED("IMG-500-006", "이미지 조회에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_PROCESSING_ERROR("IMG-500-007", "이미지 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NSFW_DETECTION_FAILED("IMG-500-008", "NSFW 감지에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ImageErrorCode(String code, String message, HttpStatus status) {
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