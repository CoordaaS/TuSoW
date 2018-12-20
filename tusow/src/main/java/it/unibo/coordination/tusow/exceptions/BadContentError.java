package it.unibo.coordination.tusow.exceptions;

public class BadContentError extends ClientError {

    private static final int CODE = 400;
    private static final String MSG = "Bad Content";

    public BadContentError() {
        super(CODE, MSG);
    }

    public BadContentError(String message) {
        super(CODE, MSG + ": " + message);
    }

    public BadContentError(String message, Throwable cause) {
        super(CODE, MSG + ": " +  message, cause);
    }

    public BadContentError(Throwable cause) {
        super(CODE, MSG, cause);
    }
}
