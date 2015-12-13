package io.barnabycolby.sqrlclient.sqrl;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.exceptions.CommandFailedException;
import io.barnabycolby.sqrlclient.exceptions.CryptographyException;
import io.barnabycolby.sqrlclient.exceptions.InvalidServerResponseException;
import io.barnabycolby.sqrlclient.exceptions.NoNutException;
import io.barnabycolby.sqrlclient.exceptions.TransientErrorException;
import io.barnabycolby.sqrlclient.exceptions.VersionNotSupportedException;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;

import java.io.IOException;

public class AccountExistsTask extends AsyncTask<SQRLUri, Void, Boolean> {
    private TextView accountExistsTextView;
    private String accountExistsString;
    private String accountDoesNotExistString;

    public AccountExistsTask(TextView accountExistsTextView, String accountExistsString, String accountDoesNotExistString) {
        this.accountExistsTextView = accountExistsTextView;
        this.accountExistsString = accountExistsString;
        this.accountDoesNotExistString = accountDoesNotExistString;
    }

    protected void onPreExecute() {
        this.accountExistsTextView.setText("Contacting server...");
        this.accountExistsTextView.setVisibility(View.VISIBLE);
    }

    protected Boolean doInBackground(SQRLUri... uris) {
        // If the caller didn't provide the right arguments
        if (uris == null || uris.length < 1) {
            throw new IllegalArgumentException();
        }

        // Extract the SQRLUri
        SQRLUri sqrlUri = uris[0];

        try {
            // Create the necessary SQRLRequest dependencies
            SQRLConnection sqrlConnection = new SQRLConnection(sqrlUri);
            SQRLIdentity sqrlIdentity = new SQRLIdentity();
            SQRLResponseFactory factory = new RealSQRLResponseFactory();
            SQRLRequest request = new SQRLRequest(sqrlConnection, sqrlIdentity, factory);

            // Perform the query and return the result
            SQRLResponse response = request.send();
            return Boolean.valueOf(response.accountExists());
        } catch (IOException | CryptographyException | VersionNotSupportedException | InvalidServerResponseException | CommandFailedException | TransientErrorException | NoNutException ex) {
            Log.e("SQRLClient", "Account exists task failed: " + ex.getMessage());
            return null;
        }
    }

    protected void onPostExecute(Boolean result) {
        String textToSet;

        if (result == null
            textToSet = "Something went wrong.";
        } else if (result.booleanValue()) {
            textToSet = this.accountExistsString;
        } else {
            textToSet = this.accountDoesNotExistString;
        }

        this.accountExistsTextView.setText(textToSet);
    }
}
