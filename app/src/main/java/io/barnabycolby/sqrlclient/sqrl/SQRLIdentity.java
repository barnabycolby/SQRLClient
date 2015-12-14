package io.barnabycolby.sqrlclient.sqrl;

import org.abstractj.kalium.*;
import android.util.Base64;
import java.nio.charset.Charset;
import io.barnabycolby.sqrlclient.exceptions.CryptographyException;

/**
 * Wraps a SQRL Identity to provide helper methods for using the identity.
 */
public class SQRLIdentity {
    private Sodium sodium;

    public SQRLIdentity() {
        // Initialise the sodium singleton
        this.sodium = NaCl.sodium();
    }

    /**
     * Gets the SQRL Identity public key.
     *
     * @return The identity key.
     */
    public String getIdentityKey() {
        // Currently this simply returns a hardcoded public key
        return "AKcDCChuFwzyHo2gm14fbuFmi27MIjUmcEXJx4pWdLo";
    }

    /**
     * Signs a message using the private key of the SQRL Identity.
     *
     * @return The signed message.
     * @throws CryptographyException  If an unrecoverable cryptographic error occurs when signing the message.
     */
    public String signUsingIdentityPrivateKey(String message) throws CryptographyException {
        // Currently this uses a hardcoded private key that matches
        // the public key used above
        String privateKey = "A3vucIkohGpHGFx7fzTTBi3BWNzeaL8EW4HeyGB22akApwMIKG4XDPIejaCbXh9u4WaLbswiNSZwRcnHilZ0ug";

        // Prepare the data so that we can pass it to the signing message
        // The signature is 512 bits, so we need to prefer a 64 byte buffer
        byte[] signedMessage = new byte[64];
        int[] signedMessageLength = new int[1];
        byte[] messageAsByteArray = message.getBytes(Charset.forName("UTF-8"));
        int messageLength = messageAsByteArray.length;
        byte[] privateKeyAsByteArray = Base64.decode(privateKey, Base64.URL_SAFE);

        // Actually sign the message
        int result = sodium.crypto_sign_ed25519(signedMessage, signedMessageLength, messageAsByteArray, messageLength, privateKeyAsByteArray);

        // Check for errors
        if (result < 0) {
            throw new CryptographyException();
        }

        String signedMessageAsString = Base64.encodeToString(signedMessage, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);
        return signedMessageAsString;
    }
}
