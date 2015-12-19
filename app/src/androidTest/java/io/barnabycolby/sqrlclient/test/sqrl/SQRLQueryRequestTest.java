package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.SQRLQueryRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponseFactory;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SQRLQueryRequestTest {

    /**
     * Create a subclass of SQRLQueryRequest so that we can verify it's protected methods.
     */
    private class SQRLQueryRequestPublic extends SQRLQueryRequest {
        public SQRLQueryRequestPublic(SQRLConnection connection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory) {
            super(connection, sqrlIdentity, sqrlResponseFactory);
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

    @Test
    public void commandStringShouldBeQry() throws Exception {
        // Create the required mock objects
        SQRLConnection connection = mock(SQRLConnection.class);
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLQueryRequestPublic request = new SQRLQueryRequestPublic(connection, sqrlIdentity, new MockSQRLResponseFactory());

        // Assert the command string
        Assert.assertEquals("query", request.getCommandString());
    }

    @Test
    public void areServerUnlockAndVerifyUnlockKeysRequiredShouldReturnFalse() throws Exception {
        // Create the required mock objects
        SQRLConnection connection = mock(SQRLConnection.class);
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLQueryRequestPublic request = new SQRLQueryRequestPublic(connection, sqrlIdentity, new MockSQRLResponseFactory());

        // Assert the result of areServerUnlockAndVerifyUnlockKeysRequired
        Assert.assertFalse(request.areServerUnlockAndVerifyUnlockKeysRequired());
    }
}
