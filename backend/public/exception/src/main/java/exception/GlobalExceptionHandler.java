package exception;

import exception.error_code.GlobalErrorCode;
import exception.excrptions.AuthException;
import exception.excrptions.BusinessException;
import exception.excrptions.ExternalServiceException;
import exception.excrptions.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusiness(BusinessException ex) {
        return toResponse(ex.getErrorCode());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRequest(InvalidRequestException ex) {
        return toResponse(ex.getErrorCode());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ExceptionResponse> handleExternal(ExternalServiceException ex) {
        // 외부 시스템 예외는 로그만 찍고 사용자에겐 INTERNAL ERROR 내려도 됨
        log.error("External failure: {}", ex.getErrorCode().getMessage());
        return toResponse(GlobalErrorCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> handleAuth(AuthException ex) {
        return toResponse(ex.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return toResponse(GlobalErrorCode.INTERNAL_ERROR);
    }

    private ResponseEntity<ExceptionResponse> toResponse(CustomErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ExceptionResponse.from(errorCode));
    }
}
