package it.unibo.coordination.tusow.exceptions;

public class NotFoundError extends ClientError {

    private static final int CODE = 404;
    private static final String MSG = "Not Found";

    public NotFoundError() {
        super(CODE, MSG);
    }

    public NotFoundError(String message) {
        super(CODE, MSG + ": " + message);
    }

    public NotFoundError(String message, Throwable cause) {
        super(CODE, MSG + ": " +  message, cause);
    }

    public NotFoundError(Throwable cause) {
        super(CODE, MSG, cause);
    }
}
