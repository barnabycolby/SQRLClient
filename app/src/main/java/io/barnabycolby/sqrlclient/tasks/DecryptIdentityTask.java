package io.barnabycolby.sqrlclient.tasks;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IncorrectPasswordException;
import io.barnabycolby.sqrlclient.helpers.TestableAsyncTask;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;

import java.security.GeneralSecurityException;

/**
 * Attempts to retrieve a SQRLIdentity using a given password.
 */
public class DecryptIdentityTask extends TestableAsyncTask<String, Integer, Boolean> {
    private PasswordCryptListener mListener;
    private SQRLUri mUri;
    private int mProgress = 0;
    private SQRLIdentity mIdentity;

    /**
     * Constructs a new instance of this class.
     *
     * @param listener  The listener to use for progress and results callbacks.
     * @param uri  The returned identity will be primed for this site.
     */
    public DecryptIdentityTask(PasswordCryptListener listener, SQRLUri uri) {
        this.mListener = listener;
        this.mUri = uri;
    }

    protected Boolean doInBackground(String... passwords) {
        // Extract the password
        if (passwords.length < 1) {
            return new Boolean(false);
        }
        String password = passwords[0];

        // Decrypt the identity
        try {
            this.mIdentity = App.getSQRLIdentityManager().getCurrentIdentityForSite(this.mUri, password, this.mListener);
        } catch (IncorrectPasswordException | GeneralSecurityException ex) {
            return new Boolean(false);
        }

        return new Boolean(true);
    }

    protected void onPostExecute(Boolean result) {
        this.mListener.onPasswordCryptResult(result.booleanValue());

        // We must call this because we are using the TestableAsyncTask
        executionFinished();
    }

    /**
     * Gets the latest published progress value.
     *
     * @return The 0-100 progress value.
     */
    public int getProgress() {
        return this.mProgress;
    }

    public SQRLIdentity getSQRLIdentity() {
        return this.mIdentity;
    }
}
