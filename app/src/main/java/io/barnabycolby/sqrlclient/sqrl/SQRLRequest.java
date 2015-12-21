package io.barnabycolby.sqrlclient.sqrl;

import android.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

import io.barnabycolby.sqrlclient.exceptions.*;

/**
 * Implements common functionality of SQRL requests, allowing easy implementation of new SQRL requests.
 */
public abstract class SQRLRequest {

    private SQRLConnection sqrlConnection;
    private SQRLIdentity sqrlIdentity;
    private SQRLResponseFactory sqrlResponseFactory;
    private HttpURLConnection connection;
    private SQRLResponse previousResponse;

    /**
     * Constructs a new SQRLRequest object.
     *
     * @param sqrlConnection  The SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     */
    public SQRLRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory) {
        this.sqrlConnection = sqrlConnection;
        this.sqrlIdentity = sqrlIdentity;
        this.sqrlResponseFactory = sqrlResponseFactory;
    }

    /**
     * Constructs a new SQRLRequest object using the previous response to chain the requests appropriately.
     *
     * @param sqrlConnection  The SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param previousResponse  The previous response sent by the server.
     */
    public SQRLRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, SQRLResponse previousResponse) throws MalformedURLException, NoNutException, IOException {
        this.sqrlConnection = sqrlConnection;
        this.sqrlIdentity = sqrlIdentity;
        this.sqrlResponseFactory = sqrlResponseFactory;
        this.previousResponse = previousResponse;

        // The connection needs to be updated using the qry value from the last server response
        this.sqrlConnection.updatePathAndQuery(this.previousResponse.getQry());
    }

    /**
     * Gets the value of the cmd parameter to be sent in the client value of the request.
     *
     * @return The cmd parameter value.
     */
    protected abstract String getCommandString();

    /**
     * Indicates whether the server unlock key and verify unlock key should be included in the client request.
     *
     * @return True if the keys should be included, false otherwise.
     */
    protected abstract boolean areServerUnlockAndVerifyUnlockKeysRequired();

    /**
     * Generates the value of the client parameter that forms part of the request.
     *
     * @return The value of the client parameter that forms part of the request.
     */
    private String getClientValue() {
        // Protocol is in version 1 at the moment
        String clientValue = "ver=1\r\n";

        // Add cmd and idk values
        clientValue += "cmd=" + getCommandString() + "\r\n";
        clientValue += "idk=" + this.sqrlIdentity.getIdentityKey() + "\r\n";

        // If the suk and vuk should be sent
        if (this.areServerUnlockAndVerifyUnlockKeysRequired()) {
            clientValue += "suk=" + this.sqrlIdentity.getServerUnlockKey() + "\r\n";
            clientValue += "vuk=" + this.sqrlIdentity.getVerifyUnlockKey() + "\r\n";
        }

        return base64Encode(clientValue);
    }

    /**
     * A helper function that base64url encodes a given string.
     *
     * @param stringToEncode The string to encode.
     * @return The encoded string.
     */
    private String base64Encode(String stringToEncode) {
        byte[] stringAsByteArray = stringToEncode.getBytes(Charset.forName("UTF-8"));
        String base64EncodedString = Base64.encodeToString(stringAsByteArray, Base64.NO_PADDING | Base64.URL_SAFE | Base64.NO_WRAP);
        return base64EncodedString;
    }

    /**
     * Generates the value of the server parameter that forms part of the request.
     */
    private String getServerValue() {
        if (this.previousResponse == null) {
            String fullUri = this.sqrlConnection.getSQRLUri().getFullUriAsString();
            return base64Encode(fullUri);
        } else {
            return this.previousResponse.toString();
        }
    }

    /**
     * Generates and sends the request to the SQRL server, returning a SQRLResponse object that can be used to easily inspect the servers response.
     *
     * @throws MalformedURLException  If the URL used to communicate with the server was malformed.
     * @throws IOException  If an IO error occurs during communication.
     * @throws SQRLException  If the servers response resulted in an unrecoverable error.
     */
    public SQRLResponse send() throws MalformedURLException, IOException, SQRLException {
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

    /**
     * Generates the request and sends it using the given outputStreamWriter.
     * 
     * @param outputStreamWriter The OutputStreamWriter to write the request to.
     * @param clientValue  The client value of the request.
     * @param serverValue  The server value of the request.
     *
     * @throws IOException  If an IO error occurs when writing the request to the OutputStreamWriter.
     * @throws CryptographyException  If the request could not be signed using the SQRL Identity.
     */
    private void generateAndSendRequest(OutputStreamWriter outputStreamWriter, String clientValue, String serverValue) throws IOException, CryptographyException {
        // Create the request content
        String idsValue = this.sqrlIdentity.signUsingIdentityPrivateKey(clientValue + serverValue);
        String body = "client=" + clientValue;
        body += "&server=" + serverValue;
        body += "&ids=" + idsValue;

        // Calling this should theoretically reduce latency according to documentation
        this.sqrlConnection.setFixedLengthStreamingMode(body.length());

        // Write the request
        outputStreamWriter.write(body);
        outputStreamWriter.flush();
    }
}
