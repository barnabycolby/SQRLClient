package io.barnabycolby.sqrlclient.exceptions;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

/**
 * Thrown by EntropyCollector when the given camera does not support the raw image format.
 */
public class RawUnsupportedException extends SQRLException {

    public RawUnsupportedException() {
        super(App.getApplicationResources().getString(R.string.raw_unsupported));
    }
}
