package io.barnabycolby.sqrlclient.exceptions;

/**
 * Signifies that the SQRL server is using a version not supported by this client.
 */
public class VersionNotSupportedException extends SQRLException {

    /**
     * Constructor that takes the version value from the servers response so that it can be displayed to the user.
     *
     * @param versionString  The version value sent in the servers response.
     */
    public VersionNotSupportedException(String versionString) {
        super("I don't support any of the versions listed by the server: " + versionString);
    }
}
