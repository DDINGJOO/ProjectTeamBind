package exception.excrptions;

import exception.CustomErrorCode;

/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
public class TokenException extends CustomBaseException {
    public TokenException(CustomErrorCode errorCode) {
        super(errorCode);
    }
}
