package io.barnabycolby.sqrlclient.tasks;

import io.barnabycolby.sqrlclient.helpers.TestableAsyncTask;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;

/**
 * Verifies a given SQRL password.
 */
public class PasswordVerificationTask extends TestableAsyncTask<String, Integer, Boolean> {
    private PasswordCryptListener mListener;
    private int mProgress = 0;

    /**
     * Constructs a new instance of this class.
     *
     * @param listener  The listener to use for progress and results callbacks.
     */
    public PasswordVerificationTask(PasswordCryptListener listener) {
        this.mListener = listener;
    }

    protected Boolean doInBackground(String... passwords) {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {}
            this.mProgress += 10;
            publishProgress(this.mProgress);
        }

        return new Boolean(true);
    }

    protected void onProgressUpdate(Integer... values) {
        this.mListener.onPasswordCryptProgressUpdate(values[0]);
    }

    protected void onPostExecute(Boolean result) {
        this.mListener.onPasswordCryptResult(result);

        // We must call this because we are using the TestableAsyncTask
        executionFinished();
    }
}
