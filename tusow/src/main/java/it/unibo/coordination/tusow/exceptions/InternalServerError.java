package it.unibo.coordination.tusow.exceptions;

public class InternalServerError extends ServerError {

    private static final int CODE = 500;

    private static final String MSG = "Internal Server Error";

    public InternalServerError() {
        super(CODE, MSG);
    }

    public InternalServerError(String message) {
        super(CODE, MSG + ": " + message);
    }

    public InternalServerError(String message, Throwable cause) {
        super(CODE, MSG + ": " +  message, cause);
    }

    public InternalServerError(Throwable cause) {
        super(CODE, MSG, cause);
    }
}
