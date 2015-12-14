package io.barnabycolby.sqrlclient.exceptions;

/**
 * This exception indicates that an unrecoverable error occurred with a SQRL cryptographic operation.
 */
public class CryptographyException extends SQRLException {

    /**
     * Constructs a new instance using a given message.
     *
     * @param message  A description of the error that caused the exception.
     */
    public CryptographyException(String message) {
        super(message);
    }
}
