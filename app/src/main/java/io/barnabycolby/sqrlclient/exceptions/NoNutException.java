package io.barnabycolby.sqrlclient.exceptions;

/**
 * Signifies that a SQRL entity did not contain the required nut value.
 *
 * The SQRL entity could be the SQRL URI or a SQRL servers response.
 */
public class NoNutException extends SQRLException {

    public NoNutException() {
        super("The required nut parameter was not present.");
    }
}
