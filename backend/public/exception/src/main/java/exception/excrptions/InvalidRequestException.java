package exception.excrptions;

import exception.CustomErrorCode;
/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
public class InvalidRequestException extends CustomBaseException {
    public InvalidRequestException(CustomErrorCode errorCode) {
        super(errorCode);
    }
}