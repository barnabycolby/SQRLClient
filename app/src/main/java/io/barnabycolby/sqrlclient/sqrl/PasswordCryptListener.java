package io.barnabycolby.sqrlclient.sqrl;

/**
 * A listener interface that should be used when performing SQRL password encryption/decryption, to receive results and progress updates.
 */
public interface PasswordCryptListener {
    /**
     * Called when the password encrypt/decrypt operation has finished.
     *
     * @param result  For encryption operations this will be true if successful and false otherwise. For decryption operations, this will be true if the password is correct and false if not.
     */
    public void onPasswordCryptResult(boolean result);

    /**
     * Called when a progress update is available.
     *
     * @param progress  The new progress value (0-100).
     */
    public void onPasswordCryptProgressUpdate(int progress);
}
