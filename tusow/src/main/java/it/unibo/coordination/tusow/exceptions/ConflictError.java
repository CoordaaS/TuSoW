package it.unibo.coordination.tusow.exceptions;

public class ConflictError extends ClientError {

    private static final int CODE = 409;
    private static final String MSG = "Conflict";

    public ConflictError() {
        super(CODE, MSG);
    }

    public ConflictError(String message) {
        super(CODE, MSG + ": " + message);
    }

    public ConflictError(String message, Throwable cause) {
        super(CODE, MSG + ": " +  message, cause);
    }

    public ConflictError(Throwable cause) {
        super(CODE, MSG, cause);
    }
}
