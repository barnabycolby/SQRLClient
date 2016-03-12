package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a given password was incorrect.
 */
public class IncorrectPasswordException extends SQRLException {

    public IncorrectPasswordException() {
        super(App.getApplicationResources().getString(R.string.incorrect_password));
    }
}
