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
     * @throws SQRLException  If the server response indicated that we could not continue, perhaps because it was invalid or because it sent an error flag.
     * @throws TransientErrorException  If the servers response indicated a transient error. This should contain information that provides an opportunity for the request to be reissued.
     */
    public SQRLResponse create(SQRLConnection connection) throws IOException, SQRLException, TransientErrorException;
}
