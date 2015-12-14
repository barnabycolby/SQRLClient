package io.barnabycolby.sqrlclient.sqrl;

import java.io.IOException;
import java.net.MalformedURLException;

public class SQRLRequestFactory {
    private SQRLUri sqrlUri;

    public SQRLRequestFactory(SQRLUri sqrlUri) {
        this.sqrlUri = sqrlUri;
    }

    public SQRLRequest create() throws MalformedURLException, IOException {
        SQRLConnection sqrlConnection = new SQRLConnection(this.sqrlUri);
        SQRLIdentity sqrlIdentity = new SQRLIdentity();
        SQRLResponseFactory factory = new RealSQRLResponseFactory();
        return new SQRLRequest(sqrlConnection, sqrlIdentity, factory);
    }
}
