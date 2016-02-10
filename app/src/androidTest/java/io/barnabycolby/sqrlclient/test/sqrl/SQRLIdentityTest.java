package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;

import io.barnabycolby.sqrlclient.exceptions.InvalidMasterKeyException;
import io.barnabycolby.sqrlclient.sqrl.*;
import io.barnabycolby.sqrlclient.test.Helper;
import io.barnabycolby.sqrlclient.test.Helper.Lambda;

import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class SQRLIdentityTest {
    @Test
    public void constructorThrowsIfMasterKeyIsWrongLengthOrNull() throws Exception {
        final SQRLUri uri = mock(SQRLUri.class);
        Helper.assertExceptionThrown(NullPointerException.class, new Lambda() {
            public void run() throws Exception {
                new SQRLIdentity(null, uri);
            }
        });

        Helper.assertExceptionThrown(InvalidMasterKeyException.class, new Lambda() {
            public void run() throws Exception {
                new SQRLIdentity(new byte[12], uri);
            }
        });
    }

    @Test
    public void constructorThrowsIfUriIsNull() throws Exception {
        Helper.assertExceptionThrown(NullPointerException.class, new Lambda() {
            public void run() throws Exception {
                new SQRLIdentity(new byte[32], null);
            }
        });
    }

    @Test
    public void correctlySignMessageUsingIdentityPrivateKey() throws Exception {
        SQRLIdentity sqrlIdentity = new SQRLIdentity(new byte[32], mock(SQRLUri.class));
        String actual = sqrlIdentity.signUsingIdentityPrivateKey("bakedbeans");
        String expected = "dqyBNmVYClimJA3tzRyzzYZlwcphlcMnQ9OkVzeKjozpIAeT3QL60JcJ7w3UdEheyWEOOMsIXgYKh4DqVLYLDA";
        Assert.assertEquals(expected, actual);
    }
}
