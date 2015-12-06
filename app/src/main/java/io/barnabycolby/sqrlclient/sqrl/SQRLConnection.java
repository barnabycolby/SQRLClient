package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SQRLConnection {
    private HttpURLConnection connection;
    private SQRLUri sqrlUri;

    public SQRLConnection(SQRLUri sqrlUri) throws MalformedURLException, IOException {
        this.sqrlUri = sqrlUri;

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

    public SQRLUri getSQRLUri() {
        return this.sqrlUri;
    }

    public HttpURLConnection getConnection() {
        return this.connection;
    }

    public void updatePathAndQuery(String newQuery) throws MalformedURLException, NoNutException {
        this.sqrlUri.updatePathAndQuery(newQuery);
    }
}
