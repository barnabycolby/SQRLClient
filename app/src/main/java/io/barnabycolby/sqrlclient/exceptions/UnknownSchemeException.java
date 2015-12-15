package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a URI did not have a recognised scheme.
 *
 * Recognised schemes include sqrl and qrl.
 */
public class UnknownSchemeException extends SQRLException {
    private String scheme;

    /**
     * Constructs an instance using the scheme to produce an informative error message.
     *
     * @param scheme  The scheme that was not recognised.
     */
    public UnknownSchemeException(String scheme) {
        super(App.getApplicationResources().getString(R.string.unknown_scheme, scheme));
        this.scheme = scheme;
    }

    /**
     * Gets the scheme that was not recognised.
     */
    public String getScheme() {
        return this.scheme;
    }
}
