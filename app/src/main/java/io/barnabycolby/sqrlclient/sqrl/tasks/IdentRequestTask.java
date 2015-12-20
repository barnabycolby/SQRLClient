package io.barnabycolby.sqrlclient.sqrl.tasks;

import android.widget.TextView;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.helpers.TestableAsyncTask;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;

import java.io.IOException;

/**
 * An AsyncTask that sends an ident request to a SQRL server.
 */
public class IdentRequestTask extends TestableAsyncTask<Void, Void, String> {
    private SQRLRequestFactory mRequestFactory;
    private TextView mTextView;
    private SQRLResponse mPreviousResponse;

    /**
     * Constructs a new instance of the IdentRequestTask.
     *
     * @param requestFactory  The request factory used to generate the ident reqeust.
     * @param textView  This text view will be used to indicate progress and the results of the ident request.
     * @param previousResponse  The last response sent, required for the creation of the IdentRequest.
     */
    public IdentRequestTask(SQRLRequestFactory requestFactory, TextView textView, SQRLResponse previousResponse) {
        this.mRequestFactory = requestFactory;
        this.mTextView = textView;
        this.mPreviousResponse = previousResponse;
    }

    protected void onPreExecute() {
        String contactingServerText = App.getApplicationResources().getString(R.string.contacting_server);
        this.mTextView.setText(contactingServerText);
    }

    protected String doInBackground(Void... params) {
        try {
            SQRLIdentRequest request = mRequestFactory.createIdent(mPreviousResponse);
            request.send();
        } catch (IOException | SQRLException ex) {
            return App.getApplicationResources().getString(R.string.authorisation_request_failed);
        }

        return App.getApplicationResources().getString(R.string.authorisation_request_sent);
    }

    protected void onPostExecute(String result) {
        this.mTextView.setText(result);

        // We must call this as we are implementing TestableAsyncTask and not AsyncTask
        this.executionFinished();
    }
}
