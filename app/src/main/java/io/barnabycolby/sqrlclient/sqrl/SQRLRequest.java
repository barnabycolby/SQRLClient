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

    public SQRLResponse send() throws MalformedURLException, IOException, CryptographyException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException, NoNutException {
        // Get the output stream as a writer to make our life easier
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.sqrlConnection.getOutputStream(), "UTF-8");

        String clientValue = getClientValue();
        generateAndSendRequest(outputStreamWriter, clientValue, getServerValue());

        SQRLResponse response;
        String lastServerResponse;
        try {
            response = this.sqrlResponseFactory.create(this.sqrlConnection);
            return response;
        } catch (TransientErrorException ex) {
            // Update the connection to use the new qry value retrieved by the response
            this.sqrlConnection.updatePathAndQuery(ex.getQry());
            outputStreamWriter = new OutputStreamWriter(this.sqrlConnection.getOutputStream(), "UTF-8");

            // Store the last server response so that we can access it later
            lastServerResponse = ex.getLastServerResponse();

            // Now we try one last time to contact the server
        }

        generateAndSendRequest(outputStreamWriter, clientValue, lastServerResponse);
        return this.sqrlResponseFactory.create(this.sqrlConnection);
    }

    private void generateAndSendRequest(OutputStreamWriter outputStreamWriter, String clientValue, String serverValue) throws IOException, CryptographyException {
        String idsValue = this.sqrlIdentity.signUsingIdentityPrivateKey(clientValue + serverValue);

        outputStreamWriter.write("client=" + clientValue);
        outputStreamWriter.write("&server=" + serverValue);
        outputStreamWriter.write("&ids=" + idsValue);
        outputStreamWriter.flush();
    }
}
