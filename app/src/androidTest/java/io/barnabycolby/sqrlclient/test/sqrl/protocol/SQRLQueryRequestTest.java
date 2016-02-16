package io.barnabycolby.sqrlclient.test.sqrl.protocol;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.sqrl.factories.SQRLConnectionFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLQueryRequest;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLResponseFactory;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SQRLQueryRequestTest {

    private SQRLConnectionFactory mConnectionFactory;
    private SQRLIdentity mIdentity;

    /**
     * Create a subclass of SQRLQueryRequest so that we can verify it's protected methods.
     */
    private class SQRLQueryRequestPublic extends SQRLQueryRequest {
        public SQRLQueryRequestPublic(SQRLConnectionFactory connectionFactory, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory) throws MalformedURLException, IOException {
            super(connectionFactory, sqrlIdentity, sqrlResponseFactory);
        }

        @Override
        public boolean areServerUnlockAndVerifyUnlockKeysRequired() {
            return super.areServerUnlockAndVerifyUnlockKeysRequired();
        }

        @Override
        public String getCommandString() {
            return super.getCommandString();
        }
    }

    @Before
    public void setUp() throws Exception {
        this.mConnectionFactory = mock(SQRLConnectionFactory.class);
        this.mIdentity = mock(SQRLIdentity.class);
    }

    @Test
    public void commandStringShouldBeQry() throws Exception {
        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLQueryRequestPublic request = new SQRLQueryRequestPublic(mConnectionFactory, mIdentity, new MockSQRLResponseFactory());

        // Assert the command string
        Assert.assertEquals("query", request.getCommandString());
    }

    @Test
    public void areServerUnlockAndVerifyUnlockKeysRequiredShouldReturnFalse() throws Exception {
        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLQueryRequestPublic request = new SQRLQueryRequestPublic(mConnectionFactory, mIdentity, new MockSQRLResponseFactory());

        // Assert the result of areServerUnlockAndVerifyUnlockKeysRequired
        Assert.assertFalse(request.areServerUnlockAndVerifyUnlockKeysRequired());
    }
}
