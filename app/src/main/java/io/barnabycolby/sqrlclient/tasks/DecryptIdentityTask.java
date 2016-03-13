package io.barnabycolby.sqrlclient.tasks;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IncorrectPasswordException;
import io.barnabycolby.sqrlclient.helpers.TestableAsyncTask;
import io.barnabycolby.sqrlclient.sqrl.DecryptIdentityListener;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;

import java.security.GeneralSecurityException;

/**
 * Attempts to retrieve a SQRLIdentity using a given password.
 */
public class DecryptIdentityTask extends TestableAsyncTask<String, Integer, SQRLIdentity> {
    private DecryptIdentityListener mListener;
    private SQRLUri mUri;
    private int mProgress = 0;

    /**
     * Constructs a new instance of this class.
     *
     * @param listener  The listener to use for progress and results callbacks.
     */
    public DecryptIdentityTask(DecryptIdentityListener listener, SQRLUri uri) {
        this.mListener = listener;
        this.mUri = uri;
    }

    protected SQRLIdentity doInBackground(String... passwords) {
        // Extract the password
        if (passwords.length < 1) {
            return null;
        }
        String password = passwords[0];

        // Decrypt the identity
        SQRLIdentity identity = null;
        try {
            identity = App.getSQRLIdentityManager().getCurrentIdentityForSite(this.mUri, password);
        } catch (IncorrectPasswordException | GeneralSecurityException ex) {
            return null;
        }

        return identity;
    }

    protected void onProgressUpdate(Integer... values) {
        this.mListener.onIdentityDecryptionProgressUpdate(values[0]);
    }

    protected void onPostExecute(SQRLIdentity identity) {
        this.mListener.onIdentityDecrypted(identity);

        // We must call this because we are using the TestableAsyncTask
        executionFinished();
    }

    /**
     * Gets the latest published progress value.
     */
    public int getProgress() {
        return this.mProgress;
    }
}
