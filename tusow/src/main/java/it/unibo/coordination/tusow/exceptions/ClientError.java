package it.unibo.coordination.tusow.exceptions;

public class ClientError extends HttpError {

    public ClientError(int statusCode) {
        super(statusCode);
    }

    public ClientError(int statusCode, String message) {
        super(statusCode, message);
    }

    public ClientError(int statusCode, String message, Throwable cause) {
        super(statusCode, message, cause);
    }
}
