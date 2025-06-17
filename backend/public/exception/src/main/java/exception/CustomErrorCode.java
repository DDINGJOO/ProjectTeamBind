package exception;

import org.springframework.http.HttpStatus;
/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
public interface CustomErrorCode {
    String getCode();            // 예: "COMMON-001"
    String getMessage();         // 예: "잘못된 요청입니다."
    HttpStatus getStatus();      // 예: HttpStatus.BAD_REQUEST
}
