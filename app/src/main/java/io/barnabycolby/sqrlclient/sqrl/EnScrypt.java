package io.barnabycolby.sqrlclient.sqrl;

import eu.artemisc.stodium.Scrypt;
import eu.artemisc.stodium.Stodium;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.helpers.Helper;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;

import java.nio.charset.Charset;

import org.abstractj.kalium.Sodium;

/**
 * An implementation of the EnScrypt password based key derivation function, described in detail at https://www.grc.com/sqrl/scrypt.htm.
 */
public class EnScrypt {
    static {
        Stodium.StodiumInit();
    }

    private int mIterations;

    private enum OperationCount { ITERATIONS, SECONDS };

    private PasswordCryptListener mListener;

    /**
     * Default constructor, should be used when you don't need to listen for progress updates.
     */
    public EnScrypt() {}

    /**
     * Use this constructor when you need to listen to progress updates.
     */
    public EnScrypt(PasswordCryptListener listener) {
        this.mListener = listener;
    }

    /**
     * Performs an EnScrypt key derivation on the given password, using the given password, salt and number of iterations.
     *
     * @param password  The password to derive a key from.
     * @param salt  The salt to use.
     * @param iterations  The number of SCRYPT iterations that should be performed.
     */
    public String deriveKey(String password, String salt, int iterations) throws SecurityException {
        return deriveKey(password, salt, OperationCount.ITERATIONS, iterations);
    }

    /**
     * Performs an EnScrypt key derivation on the given password, using the given password, salt and number of iterations.
     *
     * @param password  The password to derive a key from.
     * @param salt  The salt to use.
     * @param iterations  The number of SCRYPT iterations that should be performed.
     */
    public byte[] deriveKey(String password, byte[] salt, int iterations) throws SecurityException {
        return deriveKey(password, salt, OperationCount.ITERATIONS, iterations);
    }

    /**
     * Performs an EnScrypt key derivation for 5 seconds, using the given password and salt.
     *
     * @param password  The password to derive a key from.
     * @param salt  The salt to use.
     */
    public String deriveKeyFor5Seconds(String password, String salt) throws SecurityException {
        return deriveKey(password, salt, OperationCount.SECONDS, 5);
    }

    /**
     * Performs an EnScrypt key derivation for 5 seconds, using the given password and salt.
     *
     * @param password  The password to derive a key from.
     * @param salt  The salt to use.
     */
    public byte[] deriveKeyFor5Seconds(String password, byte[] salt) throws SecurityException {
        return deriveKey(password, salt, OperationCount.SECONDS, 5);
    }

    /**
     * Performs an EnScrypt key derivation on the given password, using the given parameters.
     *
     * @param password  The password to derive a key from.
     * @param salt  The salt to use.
     * @param operationType  Whether the count is in terms of no. of iterations of no. of seconds.
     * @param count  The count of iterations or seconds to perform.
     */
    private String deriveKey(String password, String salt, OperationCount operationType, int count) {
        byte[] saltAsByteArray = salt == null ? null : Helper.hexStringToByteArray(salt);
        byte[] key = deriveKey(password, saltAsByteArray, operationType, count);
        return byteArrayToHexString(key);
    }

    /**
     * Performs an EnScrypt key derivation on the given password, using the given parameters.
     *
     * @param password  The password to derive a key from.
     * @param salt  The salt to use.
     * @param operationType  Whether the count is in terms of no. of iterations of no. of seconds.
     * @param count  The count of iterations or seconds to perform.
     */
    private byte[] deriveKey(String password, byte[] salt, OperationCount operationType, int count) {
        // Handle null arguments
        if (password == null) {
            password = "";
        }
        if (salt == null) {
            salt = new byte[0];
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

        // Perform the chaining of the scrypt operations
        byte[] scryptOutput = salt;
        long startTime = System.currentTimeMillis();
        int numberOfIterationsPerformed = 0;
        while (true) {
            scryptOutput = scryptDeriveKey(passwordAsByteArray, scryptOutput);
            key = xorByteArrays(key, scryptOutput);
            numberOfIterationsPerformed++;

            if (operationType.equals(OperationCount.ITERATIONS)) {
                // If we have a listener, we need to give it a progress update
                if (this.mListener != null) {
                    int progress = (numberOfIterationsPerformed * 100) / count;
                    this.mListener.onPasswordCryptProgressUpdate(progress);
                }

                if (numberOfIterationsPerformed == count) {
                    break;
                }
            } else if (operationType.equals(OperationCount.SECONDS)) {
                long duration = System.currentTimeMillis() - startTime;
                if (duration >= count * 1000) {
                    break;
                }
            }
        }

        // Store the number of iterations performed, this will be required by the caller if using deriveKeyFor5Seconds
        this.mIterations = numberOfIterationsPerformed;

        return key;
    }

    /**
     * Performs a single iteration of the Scrypt key derivation algorithm.
     *
     * @param password  The password to derive a key from, or a previous output from scrypt.
     * @param salt  The salt to use.
     */
    private byte[] scryptDeriveKey(byte[] password, byte[] salt) {
        byte[] key = new byte[32];
        Stodium.checkStatus(Sodium.crypto_pwhash_scryptsalsa208sha256_ll(password, password.length, salt, salt.length, 512, 256, 1, key, key.length));
        return key;
    }

    private String byteArrayToHexString(byte[] array) {
        StringBuilder builder = new StringBuilder();
        for (byte b : array) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    private byte[] xorByteArrays(byte[] xs, byte[] ys) {
        byte[] result = new byte[xs.length];
        for (int i = 0; i < xs.length; i++) {
            int x = (int)xs[i];
            int y = (int)ys[i];
            int xor = x ^ y;
            result[i] = (byte)(0xff & xor);
        }
        return result;
    }

    public int getIterations() {
        return this.mIterations;
    }
}
