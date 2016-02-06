package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a identity could not be deleted.
 */
public class IdentityCouldNotBeDeletedException extends SQRLException {

    /**
     * Constructs a new instance of this exception.
     *
     * @param identityName  The name of the identity that could not deleted.
     */
    public IdentityCouldNotBeDeletedException(String identityName) {
        super(App.getApplicationResources().getString(R.string.identity_could_not_be_deleted, identityName));
    }
}
