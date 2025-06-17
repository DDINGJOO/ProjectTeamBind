package exception.excrptions;

import exception.CustomErrorCode;
import lombok.Getter;
/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
@Getter
public abstract class CustomBaseException extends RuntimeException {
    private final CustomErrorCode errorCode;

    public CustomBaseException(CustomErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}