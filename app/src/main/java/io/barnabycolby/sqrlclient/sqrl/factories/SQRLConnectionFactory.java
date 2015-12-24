package io.barnabycolby.sqrlclient.sqrl.factories;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;

import java.io.IOException;
import java.net.MalformedURLException;

public class SQRLConnectionFactory {
    private SQRLUri mUri;

    public SQRLConnectionFactory(SQRLUri uri) {
        this.mUri = uri;
    }

    public SQRLConnection create() throws MalformedURLException, IOException {
        return new SQRLConnection(mUri);
    }

    public SQRLConnection create(String pathAndQuery) throws MalformedURLException, IOException, NoNutException {
        this.mUri.updatePathAndQuery(pathAndQuery);
        return this.create();
    }
}
