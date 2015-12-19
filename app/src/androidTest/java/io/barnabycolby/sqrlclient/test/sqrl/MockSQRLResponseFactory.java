package io.barnabycolby.sqrlclient.test.sqrl;

import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.exceptions.TransientErrorException;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponseFactory;

import java.io.IOException;

import static org.mockito.Mockito.mock;

public class MockSQRLResponseFactory implements SQRLResponseFactory {
    @Override
    public SQRLResponse create(SQRLConnection connection) throws IOException, SQRLException, TransientErrorException {
        return mock(SQRLResponse.class);
    }
}
