package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;

import java.io.IOException;

/**
 * A factory that can be used to create a SQRLResponse.
 */
public interface SQRLResponseFactory {
    /**
     * Creates a SQRLResponse using the given connection.
     *
     * @param connection  The connection passed directly to the SQRLResponse constructor.
     * @throws IOException  If the server response could not be retrieved.
     * @throws VersionNotSupportedException  If the servers version was not supported.
     * @throws InvalidServerResponseException  If the servers response could not be parsed.
     * @throws CommandFailedException  If the servers response indicated that the command failed.
     * @throws TransientErrorException  If the servers response indicated a transient error.
     */
    public SQRLResponse create(SQRLConnection connection) throws IOException, VersionNotSupportedException, InvalidServerResponseException, CommandFailedException, TransientErrorException;
}
