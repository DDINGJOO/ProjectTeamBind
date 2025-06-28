package exception.excrptions;

import exception.CustomErrorCode;


public class BffException extends CustomBaseException {
    public BffException(CustomErrorCode errorCode) {
        super(errorCode);
    }
}
