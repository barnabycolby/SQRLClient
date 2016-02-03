package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a new identity could not be created because it could not be written to disk.
 */
public class IdentityCouldNotBeWrittenToDiskException extends SQRLException {

    public IdentityCouldNotBeWrittenToDiskException() {
        super(App.getApplicationResources().getString(R.string.identity_could_not_be_written_to_disk));
    }
}
