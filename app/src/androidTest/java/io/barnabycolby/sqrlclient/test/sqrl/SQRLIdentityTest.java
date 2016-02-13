package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import eu.artemisc.stodium.Ed25519;
import eu.artemisc.stodium.Stodium;

import java.nio.charset.Charset;

import org.abstractj.kalium.Sodium;
import org.junit.*;
import org.junit.runner.RunWith;

import io.barnabycolby.sqrlclient.exceptions.InvalidMasterKeyException;
import io.barnabycolby.sqrlclient.sqrl.*;
import io.barnabycolby.sqrlclient.test.Helper;
import io.barnabycolby.sqrlclient.test.Helper.Lambda;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class SQRLIdentityTest {
    static {
        Stodium.StodiumInit();
    }

    private byte[] mMasterKey;
    private SQRLUri mUri;
    private SQRLIdentity mIdentity;
    private String mHostName = "www.amazon.com";
    private byte[] mPublicKey;
    private byte[] mPrivateKey;

    public SQRLIdentityTest() {
        // Initialise member variables
        this.mMasterKey = Base64.decode("Z2Vyb25pbW90aWdlcmJyYWlucG9wcHlCZWFuc0VnZ3M", Base64.URL_SAFE);
        this.mPublicKey = new byte[32];
        this.mPrivateKey = new byte[64];

        // Generate the expected public key
        byte[] hmacResult = new byte[32];
        byte[] hostNameAsByteArray = this.mHostName.getBytes(Charset.forName("UTF-8"));;
        Sodium.crypto_auth_hmacsha256(hmacResult, hostNameAsByteArray, hostNameAsByteArray.length, this.mMasterKey);
        Ed25519.keypairSeed(this.mPublicKey, this.mPrivateKey, hmacResult);
    }

    @Before
    public void setUp() throws Exception {
        // Create the master key and uri
        this.mUri = mock(SQRLUri.class);
        when(this.mUri.getHost()).thenReturn(this.mHostName);

        this.mIdentity = new SQRLIdentity(this.mMasterKey, this.mUri);
    }

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
    public void identityKeyIsCorrectlyGeneratedFromMasterKeyAndUri() {
        String expected = Base64.encodeToString(this.mPublicKey, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);
        assertEquals(expected, this.mIdentity.getIdentityKey());
    }

    @Test
    public void correctlySignMessageUsingIdentityPrivateKey() throws Exception {
        String message = "bakedbeans";
        byte[] messageAsByteArray = message.getBytes(Charset.forName("UTF-8"));
        String actual = this.mIdentity.signUsingIdentityPrivateKey(message);
        byte[] expectedAsByteArray = new byte[messageAsByteArray.length + Ed25519.SIGNBYTES];
        Ed25519.sign(expectedAsByteArray, messageAsByteArray, this.mPrivateKey);
        String expected = Base64.encodeToString(expectedAsByteArray, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);
        Assert.assertEquals(expected, actual);
    }
}
