package io.barnabycolby.sqrlclient.exceptions;

public class InvalidServerResponseException extends Exception {

    public InvalidServerResponseException() {
        super("The servers response was not valid.");
    }
}
