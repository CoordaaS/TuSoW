package it.unibo.coordination.tusow.exceptions;

public class ForbiddenError extends ClientError {

    private static final int CODE = 403;
    private static final String MSG = "Forbidden";

    public ForbiddenError() {
        super(CODE, MSG);
    }

    public ForbiddenError(String message) {
        super(CODE, MSG + ": " + message);
    }

    public ForbiddenError(String message, Throwable cause) {
        super(CODE, MSG + ": " +  message, cause);
    }

    public ForbiddenError(Throwable cause) {
        super(CODE, MSG, cause);
    }
}
