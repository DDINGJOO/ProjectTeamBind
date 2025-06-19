package resposne;

import exception.CustomErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T result;

    /** 성공 응답 (데이터 포함) */
    public static <T> BaseResponse<T> success(T result) {
        return new BaseResponse<>(true, String.valueOf(0), "요청에 성공하였습니다.", result);
    }

    /** 성공 응답 (데이터 없음) */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(true, String.valueOf(0), "요청에 성공하였습니다.", null);
    }

    /** 실패 응답 - 에러 코드 기반 */
    public static <T> BaseResponse<T> fail(CustomErrorCode errorCode) {
        return new BaseResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    /** 서버 내부 오류 등 직접 메시지 설정 */
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, String.valueOf(500), message, null);
    }
}