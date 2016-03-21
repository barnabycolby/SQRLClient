package io.barnabycolby.sqrlclient.sqrl.protocol;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLConnectionFactory;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLResponseFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;

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
     * @param sqrlConnectionFactory  The factory used to craete the SQRL connection to send the request over.
     * @param sqrlIdentity  The identity to use for server communication.
     * @param sqrlResponseFactory  The factory to use when creating a new response object.
     * @param previousResponse  The last response sent by the server, this is required in order to determine whether the account exists.
     *
     * @throws MalformedURLException If the SQRLRequest constructor throws this exception.
     * @throws IOException If the SQRLRequest constructor throws this exception.
     * @throws NoNutException If the SQRLRequest constructor throws this exception.
     */
    public SQRLIdentRequest(SQRLConnectionFactory sqrlConnectionFactory, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, SQRLResponse previousResponse) throws MalformedURLException, IOException, NoNutException {
        super(sqrlConnectionFactory, sqrlIdentity, sqrlResponseFactory, previousResponse);
        this.previousResponse = previousResponse;
    }

    @Override
    protected boolean areServerUnlockAndVerifyUnlockKeysRequired() {
        return !this.previousResponse.currentAccountExists();
    }

    @Override
    protected String getCommandString() {
        return "ident";
    }
}
