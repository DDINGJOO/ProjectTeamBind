package exception.excrptions;

import exception.CustomErrorCode;


public class UserProfileException extends CustomBaseException {
    public UserProfileException(CustomErrorCode errorCode) {
        super(errorCode);
    }
}
