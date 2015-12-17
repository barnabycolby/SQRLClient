package io.barnabycolby.sqrlclient.sqrl;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * A factory to help with the creation of a SQRLRequest.
 */
public class SQRLRequestFactory {
    private SQRLUri sqrlUri;

    /**
     * Constructs a new factory using the given uri.
     *
     * @param sqrlUri  The SQRLUri used to communicate with the SQRL server.
     */
    public SQRLRequestFactory(SQRLUri sqrlUri) {
        this.sqrlUri = sqrlUri;
    }

    /**
     * Creates a new SQRLRequest object.
     *
     * @throws MalformedURLException  If the URI used to create the request is malformed. The URI is retrieved from the SQRLUri object passed in via the constructor.
     * @throws IOException  If the connection to the server could not be created.
     */
    public SQRLQueryRequest createQuery() throws MalformedURLException, IOException {
        SQRLConnection sqrlConnection = new SQRLConnection(this.sqrlUri);
        SQRLIdentity sqrlIdentity = new SQRLIdentity();
        SQRLResponseFactory factory = new RealSQRLResponseFactory();
        return new SQRLQueryRequest(sqrlConnection, sqrlIdentity, factory);
    }
}
