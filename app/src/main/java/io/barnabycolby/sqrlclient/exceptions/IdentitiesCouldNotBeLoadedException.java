package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that the SQRL identities could not be loaded from disk or created.
 *
 * This normally indicates that an identities file/folder already exists in an unexpected format.
 */
public class IdentitiesCouldNotBeLoadedException extends SQRLException {

    public IdentitiesCouldNotBeLoadedException() {
        super(App.getApplicationResources().getString(R.string.identities_could_not_be_loaded));
    }
}
