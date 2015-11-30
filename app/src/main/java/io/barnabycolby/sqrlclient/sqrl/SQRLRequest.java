package io.barnabycolby.sqrlclient.sqrl;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.*;
import java.net.URL;
import android.util.Base64;
import java.nio.charset.Charset;
import io.barnabycolby.sqrlclient.exceptions.*;

public class SQRLRequest {

    private SQRLUri sqrlUri;
    private SQRLIdentity sqrlIdentity;
    private HttpURLConnection connection;

    public SQRLRequest(SQRLUri sqrlUri, SQRLIdentity sqrlIdentity) {
        this.sqrlUri = sqrlUri;
        this.sqrlIdentity = sqrlIdentity;
    }

    public SQRLRequest(SQRLUri sqrlUri, SQRLIdentity sqrlIdentity, HttpURLConnection connection) {
        this.sqrlUri = sqrlUri;
        this.sqrlIdentity = sqrlIdentity;
        this.connection = connection;
    }

    public HttpURLConnection getConnection() throws MalformedURLException, IOException {
        // Open the connection
        if (this.connection == null) {
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

        return this.connection;
    }

    private String getClientValue() {
        // Protocol is in version 1 at the moment
        String clientValue = "ver=1\r\n";

        // Add remaining values
        clientValue += "cmd=query\r\n";
        clientValue += "idk=" + this.sqrlIdentity.getIdentityKey() + "\r\n";

        return base64Encode(clientValue);
    }

    private String base64Encode(String stringToEncode) {
        byte[] stringAsByteArray = stringToEncode.getBytes(Charset.forName("UTF-8"));
        String base64EncodedString = Base64.encodeToString(stringAsByteArray, Base64.NO_PADDING | Base64.URL_SAFE | Base64.NO_WRAP);
        return base64EncodedString;
    }

    private String getServerValue() {
        String fullUri = this.sqrlUri.getFullUriAsString();
        return base64Encode(fullUri);
    }

    public SQRLResponse send() throws MalformedURLException, IOException, CryptographyException, VersionNotSupportedException, InvalidServerResponseException {
        // Get the output stream as a writer to make our life easier
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.getConnection().getOutputStream(), "UTF-8");

        // Write the data
        String clientValue = getClientValue();
        String serverValue = getServerValue();
        String idsValue = this.sqrlIdentity.signUsingIdentityPrivateKey(clientValue + serverValue);
        outputStreamWriter.write("client=" + clientValue);
        outputStreamWriter.write("&server=" + serverValue);
        outputStreamWriter.write("&ids=" + idsValue);

        // Make sure that all the data is written
        outputStreamWriter.flush();

        return new SQRLResponse(this.getConnection());
    }
}
