package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.sqrl.SQRLConnection;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SQRLIdentRequestTest {

    /**
     * Create a subclass of SQRLIdentRequest so that we can verify it's protected methods.
     */
    private class SQRLIdentRequestPublic extends SQRLIdentRequest {
        public SQRLIdentRequestPublic(SQRLConnection connection, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, SQRLResponse previousResponse) {
            super(connection, sqrlIdentity, sqrlResponseFactory, previousResponse);
        }

        @Override
        public String getCommandString() {
            return super.getCommandString();
        }
    }

    @Test
    public void commandStringShouldBeIdent() throws Exception {
        // Create the required mock objects
        SQRLConnection connection = mock(SQRLConnection.class);
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);
        SQRLResponse previousResponse = mock(SQRLResponse.class);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLIdentRequestPublic request = new SQRLIdentRequestPublic(connection, sqrlIdentity, new MockSQRLResponseFactory(), previousResponse);

        // Assert the command string
        Assert.assertEquals(request.getCommandString(), "ident");
    }
}
