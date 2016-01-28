package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a new identity could not be created because one with the same identity name already existed.
 */
public class IdentityAlreadyExistsException extends SQRLException {

    public IdentityAlreadyExistsException() {
        super(App.getApplicationResources().getString(R.string.identity_already_exists));
    }
}
