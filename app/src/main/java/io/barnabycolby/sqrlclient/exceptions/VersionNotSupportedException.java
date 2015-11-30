package io.barnabycolby.sqrlclient.exceptions;

public class VersionNotSupportedException extends Exception {

    public VersionNotSupportedException(String versionString) {
        super("I don't support any of the versions listed by the server: " + versionString);
    }
}
