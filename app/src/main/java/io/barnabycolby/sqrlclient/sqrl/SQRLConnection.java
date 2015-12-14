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
 */
public class SQRLConnection {
    private HttpURLConnection connection;
    private SQRLUri sqrlUri;

    /**
     * Constructor takes a SQRLUri object describing the information required to initialise communication with the SQRL server.
     *
     * @param sqrlUri  The SQRLUri containing information required to initialise communication with the server.
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

    public int getResponseCode() throws IOException {
        return this.connection.getResponseCode();
    }

    public InputStream getInputStream() throws IOException {
        return this.connection.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.connection.getOutputStream();
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

    /**
     * Updates the SQRLUri object to use the new path and query sent in a server response.
     *
     * @param newQuery  The new path and query used to update the internal SQRLUri object.
     * @throws MalformedURLException  If the path and query are invalid.
     * @throws NoNutException  If the new path and query does not contain a nut value.
     * @throws IOException If the internal connection could not be disconnected.
     */
    public void updatePathAndQuery(String newQuery) throws MalformedURLException, NoNutException, IOException {
        // Ask SQRLUri to update itself
        this.sqrlUri.updatePathAndQuery(newQuery);

        // Now we need to update the connection to use the new SQRLUri
        this.connection.disconnect();
        this.initialiseConnection();
    }
}
