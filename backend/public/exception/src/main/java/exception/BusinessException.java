package exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final CustomErrorCode errorCode;

    public BusinessException(CustomErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
