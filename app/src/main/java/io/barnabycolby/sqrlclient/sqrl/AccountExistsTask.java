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
import io.barnabycolby.sqrlclient.sqrl.SQRLRequestFactory;

import java.io.IOException;

public class AccountExistsTask extends AsyncTask<Void, Void, Boolean> {
    private SQRLRequestFactory sqrlRequestFactory;
    private TextView accountExistsTextView;
    private String accountExistsString;
    private String accountDoesNotExistString;

    public AccountExistsTask(SQRLRequestFactory sqrlRequestFactory, TextView accountExistsTextView, String accountExistsString, String accountDoesNotExistString) {
        this.sqrlRequestFactory = sqrlRequestFactory;
        this.accountExistsTextView = accountExistsTextView;
        this.accountExistsString = accountExistsString;
        this.accountDoesNotExistString = accountDoesNotExistString;
    }

    protected void onPreExecute() {
        this.accountExistsTextView.setText("Contacting server...");
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

    protected void onPostExecute(Boolean result) {
        String textToSet;

        if (result == null) {
            textToSet = "Something went wrong.";
        } else if (result.booleanValue()) {
            textToSet = this.accountExistsString;
        } else {
            textToSet = this.accountDoesNotExistString;
        }

        this.accountExistsTextView.setText(textToSet);
    }
}
