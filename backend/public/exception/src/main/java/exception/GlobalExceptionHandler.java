package exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusiness(BusinessException ex) {
        CustomErrorCode code = ex.getErrorCode();
        return ResponseEntity
                .status(code.getStatus())
                .body(ExceptionResponse.from(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnexpected(Exception ex) {
        return ResponseEntity
                .status(GlobalErrorCode.INTERNAL_ERROR.getStatus())
                .body(ExceptionResponse.from(GlobalErrorCode.INTERNAL_ERROR));
    }
}
