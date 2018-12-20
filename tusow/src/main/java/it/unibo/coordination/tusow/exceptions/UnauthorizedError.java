package it.unibo.coordination.tusow.exceptions;

public class UnauthorizedError extends ClientError {

    private static final int CODE = 401;
    private static final String MSG = "Unauthorized";

    public UnauthorizedError() {
        super(CODE, MSG);
    }

    public UnauthorizedError(String message) {
        super(CODE, MSG + ": " + message);
    }

    public UnauthorizedError(String message, Throwable cause) {
        super(CODE, MSG + ": " +  message, cause);
    }

    public UnauthorizedError(Throwable cause) {
        super(CODE, MSG, cause);
    }
}
