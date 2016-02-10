package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a master key was invalid in someway, probably the wrong length.
 */
public class InvalidMasterKeyException extends SQRLException {

    public InvalidMasterKeyException() {
        super(App.getApplicationResources().getString(R.string.invalid_master_key));
    }
}
