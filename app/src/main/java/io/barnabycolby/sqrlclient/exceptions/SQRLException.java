package io.barnabycolby.sqrlclient.exceptions;

/**
 * A general SQRL exception that should be subclassed by all other SQRL exceptions.
 *
 * This allows SQRL exceptions to be caught without specifying a catch for every single possible exception type.
 */
public class SQRLException extends Exception {
    /**
     * Constructs a new exception using the given message.
     *
     * @param message  A detailed message describing the error that caused the exception.
     */
    SQRLException(String message) {
        super(message);
    }
}
