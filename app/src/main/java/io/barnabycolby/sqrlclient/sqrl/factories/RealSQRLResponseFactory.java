package io.barnabycolby.sqrlclient.sqrl.factories;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;;

import java.io.IOException;

/**
 * SQRLResponseFactory that simply passes the arguments directly to the SQRLResponse constructor.
 */
public class RealSQRLResponseFactory implements SQRLResponseFactory {
    public SQRLResponse create(SQRLConnection connection) throws SQRLException, TransientErrorException, IOException {
        return new SQRLResponse(connection);
    }
}
