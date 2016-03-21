package io.barnabycolby.sqrlclient.tasks;

import android.os.AsyncTask;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeWrittenToDiskException;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;

import java.security.GeneralSecurityException;

/**
 * Saves a new identity.
 */
public class SaveIdentityTask extends AsyncTask<Void, Integer, Boolean> {
    private String mIdentityName;
    private byte[] mMasterKey;
    private String mPassword;
    private PasswordCryptListener mListener;

    private int mProgress = 0;

    private String mErrorMessage;

    /**
     * Constructs a new instance of this class.
     *
     * @param identityName  The name of the new identity.
     * @param masterKey  The new identities master key.
     * @param password  The new identities password.
     * @param listener  The listener to use for progress and results callbacks.
     */
    public SaveIdentityTask(String identityName, byte[] masterKey, String password, PasswordCryptListener listener) {
        this.mIdentityName = identityName;
        this.mMasterKey = masterKey;
        this.mPassword = password;
        this.mListener = listener;
    }

    protected Boolean doInBackground(Void... params) {
        try {
            App.getSQRLIdentityManager().save(this.mIdentityName, this.mMasterKey, this.mPassword, this.mListener);
        } catch (IdentityAlreadyExistsException | IdentityCouldNotBeWrittenToDiskException | IdentitiesCouldNotBeLoadedException | GeneralSecurityException ex) {
            this.mErrorMessage = ex.getMessage();
            return new Boolean(false);
        }

        return new Boolean(true);
    }

    protected void onPostExecute(Boolean result) {
        this.mListener.onPasswordCryptResult(result.booleanValue());
    }

    /**
     * Gets the latest published progress value.
     *
     * @return The 0-100 progress value.
     */
    public int getProgress() {
        return this.mProgress;
    }

    /**
     * If an error occurred during execution, you can get the associated error message by calling this.
     *
     * @return The error message.
     */
    public String getErrorMessage() {
        return this.mErrorMessage;
    }
}
