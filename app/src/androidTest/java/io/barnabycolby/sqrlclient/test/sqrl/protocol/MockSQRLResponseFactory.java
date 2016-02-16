package io.barnabycolby.sqrlclient.test.sqrl.protocol;

import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.exceptions.TransientErrorException;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLConnection;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLResponseFactory;

import java.io.IOException;

import static org.mockito.Mockito.mock;

public class MockSQRLResponseFactory implements SQRLResponseFactory {
    @Override
    public SQRLResponse create(SQRLConnection connection) throws IOException, SQRLException, TransientErrorException {
        return mock(SQRLResponse.class);
    }
}
