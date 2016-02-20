package io.barnabycolby.sqrlclient.sqrl;

import eu.artemisc.stodium.Scrypt;
import eu.artemisc.stodium.Stodium;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

import java.nio.charset.Charset;

import org.abstractj.kalium.Sodium;

/**
 * An implementation of the EnScrypt password based key derivation function, described in detail at https://www.grc.com/sqrl/scrypt.htm.
 */
public class EnScrypt {
    static {
        Stodium.StodiumInit();
    }

    // This class only contains static methods
    private EnScrypt() {}

    /**
     * Performs an EnScrypt key derivation on the given password, using the given parameters.
     *
     * @param password  The password to derive a key from.
     * @param salt  The salt to use.
     * @param iterations  The number of SCRYPT iterations that should be performed.
     */
    public static String deriveKey(String password, String salt, int iterations) throws SecurityException {
        // Handle null arguments
        if (password == null) {
            password = "";
        }
        if (salt == null) {
            salt = "";
        }

        // Check that the password doesn't contain any null characters
        if (password.contains("\0")) {
            String errorMessage = App.getApplicationResources().getString(R.string.password_contains_null);
            throw new IllegalArgumentException(errorMessage);
        }

        // Append the null terminating byte to the password (part of the SQRL protocol)
        password += '\0';

        // Set up the required byte arrays
        byte[] key = new byte[32];
        byte[] passwordAsByteArray = password.getBytes(Charset.forName("UTF-8"));
        byte[] saltAsByteArray = hexStringToByteArray(salt);

        // Perform the chaining of the scrypt operations
        byte[] scryptOutput = saltAsByteArray;
        for (int i = 0; i < iterations; i++) {
            scryptOutput = scryptDeriveKey(passwordAsByteArray, scryptOutput);
            key = xorByteArrays(key, scryptOutput);
        }

        return byteArrayToHexString(key);
    }

    /**
     * Performs a single iteration of the Scrypt key derivation algorithm.
     *
     * @param password  The password to derive a key from, or a previous output from scrypt.
     * @param salt  The salt to use.
     */
    private static byte[] scryptDeriveKey(byte[] password, byte[] salt) {
        byte[] key = new byte[32];
        Stodium.checkStatus(Sodium.crypto_pwhash_scryptsalsa208sha256_ll(password, password.length, salt, salt.length, 512, 256, 1, key, key.length));
        return key;
    }

    private static String byteArrayToHexString(byte[] array) {
        StringBuilder builder = new StringBuilder();
        for (byte b : array) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    // Taken from http://stackoverflow.com/questions/8890174/in-java-how-do-i-convert-a-hex-string-to-a-byte
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] array = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            array[i / 2] = (byte)((Character.digit(hexString.charAt(i), 16) << 4)
                                 + Character.digit(hexString.charAt(i + 1), 16));
        }

        return array;
    }

    private static byte[] xorByteArrays(byte[] xs, byte[] ys) {
        byte[] result = new byte[xs.length];
        for (int i = 0; i < xs.length; i++) {
            int x = (int)xs[i];
            int y = (int)ys[i];
            int xor = x ^ y;
            result[i] = (byte)(0xff & xor);
        }
        return result;
    }
}
