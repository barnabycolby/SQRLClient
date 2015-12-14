package io.barnabycolby.sqrlclient.exceptions;

/**
 * Signifies that the SQRL server response could not be parsed because it's form was invalid.
 */
public class InvalidServerResponseException extends SQRLException {
    public InvalidServerResponseException(String message) {
        super(message);
    }
}
