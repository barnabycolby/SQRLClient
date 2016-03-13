package io.barnabycolby.sqrlclient.sqrl;

/**
 * A listener interface that should be used when asynchronously retrieving a SQRLIdentity.
 */
public interface DecryptIdentityListener {
    /**
     * Called when the identity decryption has been completed with the results.
     *
     * @param result  Returns a SQRLIdentity if the decryption was successful, and null if the password was incorrect.
     */
    public void onIdentityDecrypted(SQRLIdentity result);

    /**
     * Called when a progress update is available.
     *
     * @param progress  The new progress value (0-100).
     */
    public void onIdentityDecryptionProgressUpdate(int progress);
}
