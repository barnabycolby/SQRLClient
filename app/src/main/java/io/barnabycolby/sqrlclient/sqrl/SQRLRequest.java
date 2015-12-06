package io.barnabycolby.sqrlclient.sqrl;

import android.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

import io.barnabycolby.sqrlclient.exceptions.*;

public class SQRLRequest {

    private SQRLConnection sqrlConnection;
    private SQRLIdentity sqrlIdentity;
    private SQRLResponseFactory sqrlResponseFactory;
    private HttpURLConnection connection;

    public SQRLRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory) {
        this.sqrlConnection = sqrlConnection;
        this.sqrlIdentity = sqrlIdentity;
        this.sqrlResponseFactory = sqrlResponseFactory;
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
        String fullUri = this.sqrlConnection.getSQRLUri().getFullUriAsString();
        return base64Encode(fullUri);
    }

    public SQRLResponse send() throws MalformedURLException, IOException, CryptographyException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException {
        // Get the output stream as a writer to make our life easier
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.sqrlConnection.getOutputStream(), "UTF-8");

        // Write the data
        String clientValue = getClientValue();
        String serverValue = getServerValue();
        String idsValue = this.sqrlIdentity.signUsingIdentityPrivateKey(clientValue + serverValue);
        outputStreamWriter.write("client=" + clientValue);
        outputStreamWriter.write("&server=" + serverValue);
        outputStreamWriter.write("&ids=" + idsValue);

        // Make sure that all the data is written
        outputStreamWriter.flush();

        SQRLResponse response;
        try {
            response = this.sqrlResponseFactory.create(this.sqrlConnection);
            return response;
        } catch (TransientErrorException ex) {
            // Absorb the exception, causing the retry below
        }

        return this.sqrlResponseFactory.create(this.sqrlConnection);
    }
}
