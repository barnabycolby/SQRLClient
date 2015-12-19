package io.barnabycolby.sqrlclient.sqrl;

import java.net.HttpURLConnection;

/**
 * Creates and sends a query request to the given SQRL server.
 */
public class SQRLQueryRequest extends SQRLRequest {

    private SQRLConnection sqrlConnection;
    private SQRLIdentity sqrlIdentity;
    private SQRLResponseFactory sqrlResponseFactory;
    private HttpURLConnection connection;

    /**
     * Constructs a new SQRLRequest object.
     *
     * @param sqrlConnection  The SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     */
    public SQRLQueryRequest(SQRLConnection sqrlConnection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory) {
        super(sqrlConnection, sqrlIdentity, sqrlResponseFactory);
    }

    @Override
    protected boolean areServerUnlockAndVerifyUnlockKeysRequired() {
        return false;
    }

    @Override
    protected String getCommandString() {
        return "query";
    }
}
