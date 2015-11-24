package io.barnabycolby.sqrlclient.sqrl;

import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.URL;

public class SQRLRequest {

    private SQRLUri sqrlUri;
    private URLConnection connection;

    public SQRLRequest(SQRLUri sqrlUri) {
        this.sqrlUri = sqrlUri;
    }

    public URLConnection getConnection() throws MalformedURLException, IOException {
        // Open the connection
        if (this.connection == null) {
            URL url = new URL(this.sqrlUri.getCommunicationURL());
            this.connection = url.openConnection();

            // Set the request properties
            this.connection.setRequestProperty("Host", this.sqrlUri.getHost());
            this.connection.setRequestProperty("User-Agent", "SQRL/1");
            this.connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        }

        return this.connection;
    }

    public Object send() {
        return null;
    }
}
