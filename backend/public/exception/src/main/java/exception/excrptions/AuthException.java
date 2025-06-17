package exception.excrptions;

import exception.CustomErrorCode;
/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
public class AuthException extends CustomBaseException {
    public AuthException(CustomErrorCode errorCode) {
        super(errorCode);
    }
}
