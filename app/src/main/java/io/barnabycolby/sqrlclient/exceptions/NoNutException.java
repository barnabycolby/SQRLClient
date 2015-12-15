package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Signifies that a SQRL entity did not contain the required nut value.
 *
 * The SQRL entity could be the SQRL URI or a SQRL servers response.
 */
public class NoNutException extends SQRLException {

    public NoNutException() {
        super(App.getApplicationResources().getString(R.string.no_nut));
    }
}
