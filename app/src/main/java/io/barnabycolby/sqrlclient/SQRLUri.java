package io.barnabycolby.sqrlclient;

import android.net.Uri;

public class SQRLUri {

    private Uri uri;

    public SQRLUri(Uri uri) throws UnknownSchemeException, NoNutException {
        // Store the URI for later
        this.uri = uri;

        // Check the scheme of the URI is recognised
        String uriScheme = uri.getScheme().toLowerCase();
        if (!uriScheme.equals("sqrl") && !uriScheme.equals("qrl")) {
            throw new UnknownSchemeException(uriScheme);
        }

        // Check the URI has a nut (query string parameter)
        String nut = uri.getQueryParameter("nut");
        if (nut == null) {
            throw new NoNutException();
        }
    }
}
