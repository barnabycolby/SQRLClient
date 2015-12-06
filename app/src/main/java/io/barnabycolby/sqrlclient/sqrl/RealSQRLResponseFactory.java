package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.*;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RealSQRLResponseFactory implements SQRLResponseFactory {
    public SQRLResponse create(HttpURLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException {
        return new SQRLResponse(connection);
    }
}
