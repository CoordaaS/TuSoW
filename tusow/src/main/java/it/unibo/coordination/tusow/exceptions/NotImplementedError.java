package it.unibo.coordination.tusow.exceptions;

public class NotImplementedError extends ServerError {

    private static final int CODE = 501;

    private static final String MSG = "Not Implemented";

    public NotImplementedError() {
        super(CODE, MSG);
    }

    public NotImplementedError(String message) {
        super(CODE, MSG + ": " + message);
    }

    public NotImplementedError(String message, Throwable cause) {
        super(CODE, MSG + ": " +  message, cause);
    }

    public NotImplementedError(Throwable cause) {
        super(CODE, MSG, cause);
    }
}
