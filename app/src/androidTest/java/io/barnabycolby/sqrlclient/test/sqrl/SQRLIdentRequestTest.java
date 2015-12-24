package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.sqrl.SQRLConnectionFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponseFactory;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SQRLIdentRequestTest {

    /**
     * Create a subclass of SQRLIdentRequest so that we can verify it's protected methods.
     */
    private class SQRLIdentRequestPublic extends SQRLIdentRequest {
        public SQRLIdentRequestPublic(SQRLConnectionFactory connectionFactory, SQRLIdentity sqrlIdentity, SQRLResponseFactory sqrlResponseFactory, SQRLResponse previousResponse) throws MalformedURLException, IOException, NoNutException {
            super(connectionFactory, sqrlIdentity, sqrlResponseFactory, previousResponse);
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
    public void commandStringShouldBeIdent() throws Exception {
        // Create the required mock objects
        SQRLConnectionFactory connectionFactory = mock(SQRLConnectionFactory.class);
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);
        SQRLResponse previousResponse = mock(SQRLResponse.class);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLIdentRequestPublic request = new SQRLIdentRequestPublic(connectionFactory, sqrlIdentity, new MockSQRLResponseFactory(), previousResponse);

        // Assert the command string
        Assert.assertEquals("ident", request.getCommandString());
    }

    @Test
    public void areServerUnlockAndVerifyUnlockKeysRequiredShouldReturnFalseIfAccountExists() throws Exception {
        // Create the required mock objects
        SQRLConnectionFactory connectionFactory = mock(SQRLConnectionFactory.class);
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);
        SQRLResponse previousResponse = mock(SQRLResponse.class);
        when(previousResponse.currentAccountExists()).thenReturn(true);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLIdentRequestPublic request = new SQRLIdentRequestPublic(connectionFactory, sqrlIdentity, new MockSQRLResponseFactory(), previousResponse);

        // Assert the result of areServerUnlockAndVerifyUnlockKeysRequired
        Assert.assertFalse(request.areServerUnlockAndVerifyUnlockKeysRequired());
    }

    @Test
    public void areServerUnlockAndVerifyUnlockKeysRequiredShouldReturnTrueIfAccountDoesNotExist() throws Exception {
        // Create the required mock objects
        SQRLConnectionFactory connectionFactory = mock(SQRLConnectionFactory.class);
        SQRLIdentity sqrlIdentity = mock(SQRLIdentity.class);
        SQRLResponse previousResponse = mock(SQRLResponse.class);
        when(previousResponse.currentAccountExists()).thenReturn(false);

        // Next, instantiate a SQRLRequest object with the mocked objects
        SQRLIdentRequestPublic request = new SQRLIdentRequestPublic(connectionFactory, sqrlIdentity, new MockSQRLResponseFactory(), previousResponse);

        // Assert the result of areServerUnlockAndVerifyUnlockKeysRequired
        Assert.assertTrue(request.areServerUnlockAndVerifyUnlockKeysRequired());
    }
}
