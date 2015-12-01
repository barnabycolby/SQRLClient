package io.barnabycolby.sqrlclient.exceptions;

public class TransientErrorException extends Exception {

    public TransientErrorException(String message) {
        super(message);
    }
}
