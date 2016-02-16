package io.barnabycolby.sqrlclient.sqrl.factories;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLIdentRequest;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLQueryRequest;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;

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
    private SQRLResponse mPreviousResponse;

    /**
     * Constructs a new factory using the given uri.
     *
     * @param sqrlUri  The SQRLUri used to communicate with the SQRL server.
     */
    public SQRLRequestFactory(SQRLUri sqrlUri) {
        this.mUri = sqrlUri;
    }

    /**
     * Creates a new SQRLQueryRequest object, sends the request and returns the response.
     *
     * @return The response to the request.
     *
     * @throws MalformedURLException  If the URI used to create the request is malformed. The URI is retrieved from the SQRLUri object passed in via the constructor.
     * @throws IOException  If the connection to the server could not be created.
     * @throws SQRLException  If the send fails.
     */
    public SQRLResponse createAndSendQuery() throws MalformedURLException, IOException, SQRLException {
        SQRLQueryRequest request = new SQRLQueryRequest(getConnectionFactory(), getIdentity(), getResponseFactory());
        this.mPreviousResponse = request.send();
        
        return this.mPreviousResponse;
    }

    /**
     * Creates a new SQRLIdentRequest object, sends the request and returns the response.
     *
     * @throws MalformedURLException  If the URI used to create the request is malformed. The URI is retrieved from the SQRLUri object passed in via the constructor.
     * @throws IOException  If the connection to the server could not be created.
     * @throws NoNutException  If the qry value in the last server response did not contain a nut parameter.
     * @throws SQRLException  If the send fails.
     */
    public SQRLResponse createAndSendIdent() throws MalformedURLException, IOException, NoNutException, SQRLException {
        SQRLIdentRequest request = new SQRLIdentRequest(getConnectionFactory(), getIdentity(), getResponseFactory(), this.mPreviousResponse);
        this.mPreviousResponse = request.send();

        return this.mPreviousResponse;
    }

    private SQRLConnectionFactory getConnectionFactory() {
        if (this.mConnectionFactory == null) {
            this.mConnectionFactory = new SQRLConnectionFactory(this.mUri);
        }

        return this.mConnectionFactory;
    }

    private SQRLIdentity getIdentity() {
        if (this.mIdentity == null) {
            this.mIdentity = App.getSQRLIdentityManager().getCurrentIdentityForSite(this.mUri);
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
