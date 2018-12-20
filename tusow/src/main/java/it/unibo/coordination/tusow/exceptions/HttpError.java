package it.unibo.coordination.tusow.exceptions;

public class HttpError extends RuntimeException {

    private final int statusCode;

    public HttpError(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpError(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpError(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
