package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.AccountExistsTask;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;
import io.barnabycolby.sqrlclient.sqrl.SQRLRequestFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView friendlySiteNameTextView;
    private SQRLUri sqrlUri;
    private View confirmDenySiteButtons;
    private Resources resources;
    private TextView accountExistsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the uri from the data and the uri text box
        Intent intent = getIntent();
        Uri uri = intent.getData();
        friendlySiteNameTextView = (TextView)findViewById(R.id.FriendlySiteNameTextView);
        if (friendlySiteNameTextView == null || uri == null) {
            return;
        }

        // Store the uri in a SQRLUri so that we can query it more easily
        this.resources = getResources();
        String errorMessage;
        try {
            sqrlUri = new SQRLUri(uri);
        } catch (UnknownSchemeException ex) {
            errorMessage = resources.getString(R.string.unknown_scheme, ex.getScheme());
            friendlySiteNameTextView.setText(errorMessage);
            return;
        } catch (NoNutException ex) {
            errorMessage = resources.getString(R.string.no_nut);
            friendlySiteNameTextView.setText(errorMessage);
            return;
        }

        // Set the textview to display the URI
        friendlySiteNameTextView.setText(sqrlUri.getDisplayName());

        // Show the confirm/deny site buttons
        confirmDenySiteButtons = findViewById(R.id.ConfirmDenySiteButtons);
        if (confirmDenySiteButtons == null) {
            Log.e(TAG, "ConfirmDenySiteButtons button group was null.");
            Toast.makeText(this, resources.getString(R.string.cannot_display_buttons), Toast.LENGTH_LONG).show();
            return;
        }
        confirmDenySiteButtons.setVisibility(View.VISIBLE);

        this.accountExistsTextView = (TextView)findViewById(R.id.AccountExistsTextView);
    }

    public void denySite(View view) {
        // Set the text view to show the 'tap to proceed' message
        String noUriMessage = getResources().getString(R.string.no_uri);
        friendlySiteNameTextView.setText(noUriMessage);

        // Hide the unnecessary UI elements
        confirmDenySiteButtons.setVisibility(View.GONE);
        accountExistsTextView.setVisibility(View.GONE);
    }

    public void confirmSite(View view) {
        String accountExistsString = resources.getString(R.string.account_exists);
        String accountDoesNotExistString = resources.getString(R.string.account_does_not_exist);
        SQRLRequestFactory factory = new SQRLRequestFactory(this.sqrlUri);
        AccountExistsTask accountExistsTask = new AccountExistsTask(factory, accountExistsTextView, accountExistsString, accountDoesNotExistString);
        accountExistsTask.execute();
    }
}
