package io.barnabycolby.sqrlclient.sqrl;

import java.net.HttpURLConnection;

/**
 * Creates and sends an identity assertion request to the SQRL server.
 */
public class SQRLIdentRequest extends SQRLRequest {

    private SQRLConnection sqrlConnection;
    private SQRLIdentity sqrlIdentity;
    private SQRLResponseFactory sqrlResponseFactory;
    private HttpURLConnection connection;
    private SQRLResponse previousResponse;

    /**
     * Constructs a new SQRLIdentRequest object.
     *
     * @param sqrlConnection  The SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param previousResponse  The last response sent by the server, this is required in order to determine whether the account exists.
     */
    public SQRLIdentRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, SQRLResponse previousResponse) {
        super(sqrlConnection, sqrlIdentity, sqrlResponseFactory);
        this.previousResponse = previousResponse;
    }

    @Override
    protected boolean areServerUnlockAndVerifyUnlockKeysRequired() {
        // Not implemented yet
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getCommandString() {
        return "ident";
    }
}
