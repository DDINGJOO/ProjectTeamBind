package exception.excrptions;

import exception.CustomErrorCode;

public class WordFilterException extends CustomBaseException {
    public WordFilterException(CustomErrorCode errorCode) {
        super(errorCode);
    }
}
