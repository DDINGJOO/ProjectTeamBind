package exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void shouldContainCorrectErrorCode() {
        BusinessException ex = new BusinessException(GlobalErrorCode.INVALID_INPUT);

        assertThat(ex.getErrorCode().getCode()).isEqualTo("COMMON-001");
        assertThat(ex.getErrorCode().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void exceptionResponseShouldMapCorrectly() {
        ExceptionResponse response = ExceptionResponse.from(GlobalErrorCode.INVALID_INPUT);

        assertThat(response.code()).isEqualTo("COMMON-001");
        assertThat(response.status()).isEqualTo(400);
        assertThat(response.message()).contains("잘못된 요청");
    }
}
