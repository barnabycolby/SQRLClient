package io.barnabycolby.sqrlclient.exceptions;

/**
 * Signifies that a SQRL entity did not contain the required nut value.
 *
 * The SQRL entity could be the SQRL URI or a SQRL servers response.
 */
public class NoNutException extends Exception {

    public NoNutException() {
        super("There was no nut.");
    }
}
