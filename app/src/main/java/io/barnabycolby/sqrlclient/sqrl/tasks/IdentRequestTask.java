package io.barnabycolby.sqrlclient.sqrl.tasks;

import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.helpers.TestableAsyncTask;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentRequest;
import io.barnabycolby.sqrlclient.sqrl.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLResponse;

import java.io.IOException;

/**
 * An AsyncTask that sends an ident request to a SQRL server.
 */
public class IdentRequestTask extends TestableAsyncTask<Void, Void, Void> {
    private SQRLRequestFactory mRequestFactory;
    private SQRLResponse mPreviousResponse;

    public IdentRequestTask(SQRLRequestFactory requestFactory, SQRLResponse previousResponse) {
        this.mRequestFactory = requestFactory;
        this.mPreviousResponse = previousResponse;
    }

    protected Void doInBackground(Void... params) {
        try {
            SQRLIdentRequest request = mRequestFactory.createIdent(mPreviousResponse);
            request.send();
        } catch (IOException | SQRLException ex) {
            // Do nothing
        }
        return null;
    }
}
