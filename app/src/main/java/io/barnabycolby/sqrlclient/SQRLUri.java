package io.barnabycolby.sqrlclient;

import android.net.Uri;
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

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

    public String getDisplayName() {
        // Check for a friendly name parameter
        String friendlyNameBase64Encoded = this.uri.getQueryParameter("sfn");
        if (friendlyNameBase64Encoded == null) {
            return this.uri.getHost();
        } else {
            // The value is a UTF-8 Base64 encoded string
            // So the first job is to reverse the Base64 decoding
            byte[] friendlyNameByteArray = Base64.decode(friendlyNameBase64Encoded, Base64.URL_SAFE);

            // Next we need to turn the byte array into a string
            // taking care to use UTF-8 (as specified in SQRL docs)
            String friendlyName;
            try {
                friendlyName = new String(friendlyNameByteArray, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return this.uri.getHost();
            }

            return friendlyName;
        }
    }
}
