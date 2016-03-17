package io.barnabycolby.sqrlclient.tasks;

import android.util.Log;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.helpers.IdentRequestListener;
import io.barnabycolby.sqrlclient.helpers.SwappableTextView;
import io.barnabycolby.sqrlclient.helpers.TestableAsyncTask;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.protocol.SQRLResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * An AsyncTask that sends an ident request to a SQRL server.
 */
public class IdentRequestTask extends TestableAsyncTask<Void, Void, String> {
    private static final String TAG = IdentRequestTask.class.getName();

    private SQRLRequestFactory mRequestFactory;
    private SwappableTextView mTextView;
    private IdentRequestListener mListener;

    /**
     * Constructs a new instance of the IdentRequestTask.
     *
     * @param requestFactory  The request factory used to generate the ident reqeust.
     * @param textView  This text view will be used to indicate progress and the results of the ident request.
     */
    public IdentRequestTask(SQRLRequestFactory requestFactory, SwappableTextView textView, IdentRequestListener listener) {
        this.mRequestFactory = requestFactory;
        this.mTextView = textView;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        String contactingServerText = App.getApplicationResources().getString(R.string.contacting_server);
        this.mTextView.setText(contactingServerText);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            mRequestFactory.createAndSendIdent();
        } catch (IOException | SQRLException ex) {
            Log.e(TAG, "Ident request task failed: " + ex.getMessage());
            return App.getApplicationResources().getString(R.string.authorisation_request_failed);
        }

        return App.getApplicationResources().getString(R.string.authorisation_request_sent);
    }

    @Override
    protected void onPostExecute(String result) {
        this.mTextView.setText(result);

        // Inform the listener that we're finished
        if (this.mListener != null) {
            this.mListener.onIdentRequestFinished();
        }

        // We must call this as we are implementing TestableAsyncTask and not AsyncTask
        this.executionFinished();
    }
}
