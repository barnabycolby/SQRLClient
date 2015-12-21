package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Wraps a HttpURLConnection to make it easier to communicate with a SQRL server.
 *
 * Ideally this class would extend HttpURLConnection, but this seems like more pain than it's worth as HttpURLConnection is an abstract class
 * with no easy to find concrete subclass.
 */
public class SQRLConnection {
    private HttpURLConnection connection;
    private SQRLUri sqrlUri;

    /**
     * Constructor takes a SQRLUri object describing the information required to initialise communication with the SQRL server.
     *
     * @param sqrlUri  The SQRLUri containing information required to initialise communication with the server.
     * @throws MalformedURLException  If the url was not valid for communication.
     * @throws IOException  If an IO error occurred when creating the connection.
     */
    public SQRLConnection(SQRLUri sqrlUri) throws MalformedURLException, IOException {
        this.sqrlUri = sqrlUri;
        initialiseConnection();
    }

    /**
     * Initialises the internal connection object, setting the appropriate headers and other appropriate initialisations.
     *
     * @throws MalformedURLException  If the URL described by the SQRLUri is malformed.
     * @throws IOException  If communication with the server could not be initialised.
     */
    private void initialiseConnection() throws MalformedURLException, IOException {
        URL url = new URL(this.sqrlUri.getCommunicationURL());
        this.connection = (HttpURLConnection)url.openConnection();

        // Make sure that this is a post request
        this.connection.setRequestMethod("POST");

        // Set the request properties
        this.connection.setRequestProperty("Host", this.sqrlUri.getHost());
        this.connection.setRequestProperty("User-Agent", "SQRL/1");
        this.connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

        // Allow outgoing and incoming data
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
    }

    /**
     * Gets the SQRLUri object used for communication with the server.
     *
     * This object starts as the SQRLUri passed to the constructor, but may have changed since then.
     */
    public SQRLUri getSQRLUri() {
        return this.sqrlUri;
    }

    /**
     * Gets the connection object that this object wraps.
     */
    public HttpURLConnection getConnection() {
        return this.connection;
    }
}
