package exception.excrptions;

import exception.CustomErrorCode;

public class ImageException extends CustomBaseException {
    public ImageException(CustomErrorCode errorCode) {
        super(errorCode);
    }
}
