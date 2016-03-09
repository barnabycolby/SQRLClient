package io.barnabycolby.sqrlclient.test.sqrl;

import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.sqrl.PasswordCrypt;

import javax.crypto.AEADBadTagException;
import java.security.GeneralSecurityException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class PasswordCryptTest {

    @Test
    public void encryptFollowedByDecryptShouldReturnOriginalMasterKey() throws Exception {
        // Encrytp the master key
        PasswordCrypt passwordCrypt = new PasswordCrypt();
        byte[] masterKey = io.barnabycolby.sqrlclient.helpers.Helper.hexStringToByteArray("A001A1B086A1AD531831208011D3451E882D077EFA0215A5B37521884376156A");
        String password = "7pfRpj#YtAdP5hML";
        byte[] encryptedMasterKey = passwordCrypt.encryptMasterKey(masterKey, password);

        // Decrypt the master key
        passwordCrypt = new PasswordCrypt(passwordCrypt.getSalt(), passwordCrypt.getIterations(), passwordCrypt.getIv());
        byte[] decryptedMasterKey = passwordCrypt.decryptMasterKey(encryptedMasterKey, password);
        assertArrayEquals(masterKey, decryptedMasterKey);
    }

    @Test
    public void decryptingWithTheWrongPasswordThrowsAnAEADBadTagException() throws Exception {
        // Encrypt the master key
        PasswordCrypt passwordCrypt = new PasswordCrypt();
        byte[] masterKey = io.barnabycolby.sqrlclient.helpers.Helper.hexStringToByteArray("A001A1B086A1AD531831208011D3451E882D077EFA0215A5B37521884376156A");
        final byte[] encryptedMasterKey = passwordCrypt.encryptMasterKey(masterKey, "beans");

        // Decrypt the master key
        final PasswordCrypt passwordCrypt2 = new PasswordCrypt(passwordCrypt.getSalt(), passwordCrypt.getIterations(), passwordCrypt.getIv());
        io.barnabycolby.sqrlclient.test.Helper.assertExceptionThrown(AEADBadTagException.class, new Lambda() {
            public void run() throws GeneralSecurityException {
                passwordCrypt2.decryptMasterKey(encryptedMasterKey, "sausages");
            }
        });
    }
}
