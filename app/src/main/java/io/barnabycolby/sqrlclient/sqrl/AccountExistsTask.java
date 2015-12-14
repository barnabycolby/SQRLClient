package io.barnabycolby.sqrlclient.sqrl;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.exceptions.CommandFailedException;
import io.barnabycolby.sqrlclient.exceptions.CryptographyException;
import io.barnabycolby.sqrlclient.exceptions.InvalidServerResponseException;
import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.exceptions.TransientErrorException;
import io.barnabycolby.sqrlclient.exceptions.VersionNotSupportedException;
import io.barnabycolby.sqrlclient.helpers.TestableAsyncTask;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLRequestFactory;

import java.io.IOException;

public class AccountExistsTask extends TestableAsyncTask<Void, Void, Boolean> {
    private SQRLRequestFactory sqrlRequestFactory;
    private TextView accountExistsTextView;
    private Resources resources;

    public AccountExistsTask(SQRLRequestFactory sqrlRequestFactory, TextView accountExistsTextView, Resources resources) {
        this.sqrlRequestFactory = sqrlRequestFactory;
        this.accountExistsTextView = accountExistsTextView;
        this.resources = resources;
    }

    protected void onPreExecute() {
        String contactingServerText = this.resources.getString(R.string.contacting_server);
        this.accountExistsTextView.setText(contactingServerText);
        this.accountExistsTextView.setVisibility(View.VISIBLE);
    }

    protected Boolean doInBackground(Void... params) {
        try {
            // Perform the query and return the result
            SQRLRequest request = this.sqrlRequestFactory.create();
            SQRLResponse response = request.send();
            return Boolean.valueOf(response.accountExists());
        } catch (IOException | CryptographyException | VersionNotSupportedException | InvalidServerResponseException | CommandFailedException | TransientErrorException | NoNutException ex) {
            Log.e("SQRLClient", "Account exists task failed: " + ex.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        String textToSet;

        if (result == null) {
            textToSet = this.resources.getString(R.string.something_went_wrong);
        } else if (result.booleanValue()) {
            textToSet = this.resources.getString(R.string.account_exists);
        } else {
            textToSet = this.resources.getString(R.string.account_does_not_exist);
        }

        this.accountExistsTextView.setText(textToSet);

        // Signal that all execution has finished
        this.executionFinished();
    }
}
