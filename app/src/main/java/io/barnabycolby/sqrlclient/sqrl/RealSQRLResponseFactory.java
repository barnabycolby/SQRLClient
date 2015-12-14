package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;

import java.io.IOException;

public class RealSQRLResponseFactory implements SQRLResponseFactory {
    public SQRLResponse create(SQRLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException {
        return new SQRLResponse(connection);
    }
}