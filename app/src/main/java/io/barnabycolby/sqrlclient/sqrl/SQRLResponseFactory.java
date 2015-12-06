package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;

import java.io.IOException;

public interface SQRLResponseFactory {
    public SQRLResponse create(SQRLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException;
}
