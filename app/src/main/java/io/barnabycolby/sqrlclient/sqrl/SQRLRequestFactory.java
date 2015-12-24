package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * A factory to help with the creation of a SQRLRequest.
 */
public class SQRLRequestFactory {
    private SQRLUri mUri;
    private SQRLIdentity mIdentity;
    private SQRLResponseFactory mResponseFactory;
    private SQRLConnectionFactory mConnectionFactory;

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
        return new SQRLQueryRequest(getConnectionFactory(), getIdentity(), getResponseFactory());
    }

    public SQRLIdentRequest createIdent(SQRLResponse previousResponse) throws MalformedURLException, IOException, NoNutException {
        return new SQRLIdentRequest(getConnectionFactory(), getIdentity(), getResponseFactory(), previousResponse);
    }

    private SQRLConnectionFactory getConnectionFactory() {
        if (this.mConnectionFactory == null) {
            this.mConnectionFactory = new SQRLConnectionFactory(this.mUri);
        }

        return this.mConnectionFactory;
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
