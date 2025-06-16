package exception;

public record ExceptionResponse(
        String code,
        String message,
        int status
) {
    public static ExceptionResponse from(CustomErrorCode code) {
        return new ExceptionResponse(
                code.getCode(),
                code.getMessage(),
                code.getStatus().value()
        );
    }
}
