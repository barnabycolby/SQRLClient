package io.barnabycolby.sqrlclient.sqrl;

/**
 * A listener interface that should be used when encryping or decrypting a SQRLIdentity.
 */
public interface PasswordCryptListener {
    /**
     * Called when the identity encryption/decryption has been completed.
     *
     * @param result  Returns true if the encryption/decryption was successful, false otherwise.
     */
    public void onPasswordCryptResult(boolean result);

    /**
     * Called when a progress update is available.
     *
     * @param progress  The new progress value (0-100).
     */
    public void onPasswordCryptProgressUpdate(int progress);
}
