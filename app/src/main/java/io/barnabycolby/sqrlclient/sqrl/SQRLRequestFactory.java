package io.barnabycolby.sqrlclient.sqrl;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * A factory to help with the creation of a SQRLRequest.
 */
public class SQRLRequestFactory {
    private SQRLUri mUri;
    private SQRLConnection mConnection;
    private SQRLIdentity mIdentity;
    private SQRLResponseFactory mResponseFactory;

    /**
     * Constructs a new factory using the given uri.
     *
     * @param sqrlUri  The SQRLUri used to communicate with the SQRL server.
     */
    public SQRLRequestFactory(SQRLUri sqrlUri) {
        this.mUri = sqrlUri;
    }

    /**
     * Creates a new SQRLRequest object.
     *
     * @throws MalformedURLException  If the URI used to create the request is malformed. The URI is retrieved from the SQRLUri object passed in via the constructor.
     * @throws IOException  If the connection to the server could not be created.
     */
    public SQRLQueryRequest createQuery() throws MalformedURLException, IOException {
        return new SQRLQueryRequest(getConnection(), getIdentity(), getResponseFactory());
    }

    public SQRLIdentRequest createIdent(SQRLResponse previousResponse) throws MalformedURLException, IOException {
        return new SQRLIdentRequest(getConnection(), getIdentity(), getResponseFactory(), previousResponse);
    }

    private SQRLConnection getConnection() throws MalformedURLException, IOException {
        if (this.mConnection == null) {
            this.mConnection = new SQRLConnection(this.mUri);
        }

        return this.mConnection;
    }

    private SQRLIdentity getIdentity() {
        if (this.mIdentity == null) {
            this.mIdentity = new SQRLIdentity();
        }

        return this.mIdentity;
    }

    private SQRLResponseFactory getResponseFactory() {
        if (this.mResponseFactory == null) {
            this.mResponseFactory = new RealSQRLResponseFactory();
        }

        return this.mResponseFactory;
    }
}
