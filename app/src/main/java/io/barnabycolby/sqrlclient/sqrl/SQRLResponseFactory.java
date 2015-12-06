package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.*;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface SQRLResponseFactory {
    public SQRLResponse create(HttpURLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException;
}
