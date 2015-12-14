package io.barnabycolby.sqrlclient.sqrl;

import android.net.Uri;
import android.util.Base64;
import android.util.Patterns;

import io.barnabycolby.sqrlclient.exceptions.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * A wrapper around a Uri object that provides extra validation and helper methods related to the SQRL protocol.
 */
public class SQRLUri {

    private Uri uri;

    /**
     * Constructor that takes the Uri to wrap.
     *
     * @param uri  The Uri to wrap.
     * @throws SQRLException  If the scheme of the uri was not sqrl or qrl, or the uri did not contain a nut query parameter.
     */
    public SQRLUri(Uri uri) throws SQRLException {
        // Store the URI for later
        this.uri = uri;

        // Check the scheme of the URI is recognised
        String uriScheme = uri.getScheme().toLowerCase();
        if (!uriScheme.equals("sqrl") && !uriScheme.equals("qrl")) {
            throw new UnknownSchemeException(uriScheme);
        }

        checkUriHasNut(uri);
    }

    /**
     * Gets the display name, which is the encoded friendly name, or the hostname if this is not present.
     *
     * @return The display name.
     */
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

    /**
     * Gets the url that should be used for communication.
     *
     * The URL used for communication should be the original URI with either the http or https scheme, based on the SQRL scheme.
     *
     * @return The communication URL.
     */
    public String getCommunicationURL() {
        Uri.Builder builder = this.uri.buildUpon();
        if (this.uri.getScheme().toLowerCase().equals("sqrl")) {
            builder.scheme("https");
        } else {
            builder.scheme("http");
        }
        return builder.build().toString();
    }

    /**
     * Gets the hostname of the URI.
     */
    public String getHost() {
        return this.uri.getHost();
    }

    /**
     * Gets the URI as a string.
     */
    public String getFullUriAsString() {
        return this.uri.toString();
    }

    /**
     * Updates the path and query values of the URI.
     *
     * Updates the patha and query values of the URI using the given string.
     *
     * @param newQuery  The new path and query as a single string.
     * @throws MalformedURLException  If the path and query were invalid.
     * @throws NoNutException  If the path and query did not contain a nut query parameter.
     */
    public void updatePathAndQuery(String newQuery) throws MalformedURLException, NoNutException {
        Uri.Builder builder = this.uri.buildUpon();
        builder.clearQuery();
        
        // Split the string on the ? character, so that we can deal with the path and query separately
        String[] newQueryComponents = newQuery.split("\\?", 2);

        // Set the path
        builder.encodedPath(newQueryComponents[0]);

        // Set the query, if one has been given
        if (newQueryComponents.length > 1) {
            builder.encodedQuery(newQueryComponents[1]);
        }

        // We must check the validity of the URL, as the Uri class does not perform any validation
        // The validator only recognises certain schemes, so we must first change the scheme of our new URI
        builder.scheme("http");
        Uri newUri = builder.build();
        if (!Patterns.WEB_URL.matcher(newUri.toString()).matches()) {
            throw new MalformedURLException();
        }
        // Reset the scheme
        builder.scheme(this.uri.getScheme());
        newUri = builder.build();

        // If the Uri has a nut then we commit the change
        checkUriHasNut(newUri);
        this.uri = newUri;
    }

    /**
     * Checks if a URI has a nut query parameter value.
     *
     * @param uriToCheck  The URI to check.
     * @throws NoNutException  If the URI does not contain a nut vaule.
     */
    private void checkUriHasNut(Uri uriToCheck) throws NoNutException {
        // Check the URI has a nut (query string parameter)
        String nut = uriToCheck.getQueryParameter("nut");
        if (nut == null) {
            throw new NoNutException();
        }
    }
}
