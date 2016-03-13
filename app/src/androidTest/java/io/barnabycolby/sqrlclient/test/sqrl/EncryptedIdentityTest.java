package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.sqrl.EncryptedIdentity;

import javax.crypto.AEADBadTagException;
import java.security.GeneralSecurityException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class EncryptedIdentityTest {

    @Test
    public void encryptFollowedByDecryptShouldReturnOriginalMasterKey() throws Exception {
        // Encrytp the master key
        byte[] masterKey = io.barnabycolby.sqrlclient.helpers.Helper.hexStringToByteArray("A001A1B086A1AD531831208011D3451E882D077EFA0215A5B37521884376156A");
        String password = "7pfRpj#YtAdP5hML";
        EncryptedIdentity encryptedIdentity = EncryptedIdentity.create(masterKey, password);
        byte[] encryptedMasterKey = encryptedIdentity.getEncryptedMasterKey();

        // Decrypt the master key
        encryptedIdentity = new EncryptedIdentity(encryptedMasterKey, encryptedIdentity.getSalt(), encryptedIdentity.getIterations(), encryptedIdentity.getIv());
        byte[] decryptedMasterKey = encryptedIdentity.decrypt(password, null);
        assertArrayEquals(masterKey, decryptedMasterKey);
    }

    @Test
    public void decryptingWithTheWrongPasswordThrowsAnAEADBadTagException() throws Exception {
        // Encrypt the master key
        byte[] masterKey = io.barnabycolby.sqrlclient.helpers.Helper.hexStringToByteArray("A001A1B086A1AD531831208011D3451E882D077EFA0215A5B37521884376156A");
        EncryptedIdentity encryptedIdentity = EncryptedIdentity.create(masterKey, "beans");
        final byte[] encryptedMasterKey = encryptedIdentity.getEncryptedMasterKey();

        // Decrypt the master key
        final EncryptedIdentity encryptedIdentity2 = new EncryptedIdentity(encryptedMasterKey, encryptedIdentity.getSalt(), encryptedIdentity.getIterations(), encryptedIdentity.getIv());
        io.barnabycolby.sqrlclient.test.Helper.assertExceptionThrown(AEADBadTagException.class, new Lambda() {
            public void run() throws GeneralSecurityException {
                encryptedIdentity2.decrypt("sausages", null);
            }
        });
    }
}
