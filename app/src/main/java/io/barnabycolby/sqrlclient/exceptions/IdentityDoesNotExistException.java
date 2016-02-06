package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a new identity does not exist.
 */
public class IdentityDoesNotExistException extends SQRLException {

    /**
     * Constructs a new instance of this exception.
     *
     * @param identityName  The name of the identity that does not exist.
     */
    public IdentityDoesNotExistException(String identityName) {
        super(App.getApplicationResources().getString(R.string.identity_does_not_exist, identityName));
    }
}
