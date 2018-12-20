package it.unibo.coordination.tusow.exceptions;

public class ServerError extends HttpError {

    public ServerError(int statusCode) {
        super(statusCode);
    }

    public ServerError(int statusCode, String message) {
        super(statusCode, message);
    }

    public ServerError(int statusCode, String message, Throwable cause) {
        super(statusCode, message, cause);
    }
}
