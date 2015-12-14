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

/**
 * Activity takes a SQRL URI and provides the user with the ability to query the server to display whether an account exists or not.
 * <p>
 * SQRL Uri is passed to this activity by clicking on a sqrl:// or qrl:// hyperlink (normally a QR code) in a browser.
 * The sites friendly name is displayed to the user with the choice of sending or not sending the query request to the site.
 * If the user proceeds, then text is displayed to indicate whether the account exists.
 * </p>
 */
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

    /**
     * Called when the deny site button is clicked.
     */
    public void denySite(View view) {
        // Set the text view to show the 'tap to proceed' message
        String noUriMessage = getResources().getString(R.string.no_uri);
        friendlySiteNameTextView.setText(noUriMessage);

        // Hide the unnecessary UI elements
        confirmDenySiteButtons.setVisibility(View.GONE);
        accountExistsTextView.setVisibility(View.GONE);
    }

    /**
     * Called when the confirm site button is clicked.
     */
    public void confirmSite(View view) {
        SQRLRequestFactory factory = new SQRLRequestFactory(this.sqrlUri);
        AccountExistsTask accountExistsTask = new AccountExistsTask(factory, accountExistsTextView, resources);
        accountExistsTask.execute();
    }
}
