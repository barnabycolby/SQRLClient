package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that the SQRL server response could not be parsed because it's form was invalid.
 */
public class InvalidServerResponseException extends SQRLException {
    /**
     * Constructs a new instance using a default error message.
     */
    public InvalidServerResponseException() {
        super(App.getApplicationResources().getString(R.string.invalid_server_response));
    }

    /**
     * Constructs a new instance using a given error message.
     *
     * @param message  The error message.
     */
    public InvalidServerResponseException(String message) {
        super(message);
    }
}
