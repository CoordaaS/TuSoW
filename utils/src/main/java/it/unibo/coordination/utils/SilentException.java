package it.unibo.coordination.utils;

public class SilentException extends RuntimeException  {

    public SilentException(String message, Throwable cause) {
        super(message, cause);
    }

    public SilentException(Throwable cause) {
        super(cause);
    }

    public SilentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public <E extends Throwable> E castCause() {
        return (E) getCause();
    }
}
