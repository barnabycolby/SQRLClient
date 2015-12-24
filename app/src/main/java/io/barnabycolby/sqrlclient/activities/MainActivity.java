package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import io.barnabycolby.sqrlclient.dialogs.CreateAccountDialogFactory;
import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.tasks.IdentRequestListener;
import io.barnabycolby.sqrlclient.tasks.AccountExistsTask;
import io.barnabycolby.sqrlclient.tasks.IdentRequestTask;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;

/**
 * Activity takes a SQRL URI and provides the user with the ability to query the server to display whether an account exists or not.
 * <p>
 * SQRL Uri is passed to this activity by clicking on a sqrl:// or qrl:// hyperlink (normally a QR code) in a browser.
 * The sites friendly name is displayed to the user with the choice of sending or not sending the query request to the site.
 * If the user proceeds, then text is displayed to indicate whether the account exists.
 * </p>
 */
public class MainActivity extends AppCompatActivity implements IdentRequestListener {

    private static final String TAG = MainActivity.class.getName();
    private TextView friendlySiteNameTextView;
    private SQRLUri sqrlUri;
    private View confirmDenySiteButtons;
    private Resources resources;
    private TextView accountExistsTextView;
    private AccountExistsTask mAccountExistsTask;
    private SQRLRequestFactory mRequestFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the uri from the data and the uri text box
        Intent intent = getIntent();
        Uri uri = intent.getData();
        friendlySiteNameTextView = (TextView)findViewById(R.id.FriendlySiteNameTextView);
        if (uri == null) {
            Log.e(TAG, "Uri passed via intent was null.");
            return;
        }

        // Store the uri in a SQRLUri so that we can query it more easily
        this.resources = getResources();
        try {
            sqrlUri = new SQRLUri(uri);
        } catch (SQRLException ex) {
            String errorMessage = resources.getString(R.string.invalid_link);
            friendlySiteNameTextView.setText(errorMessage);
            Log.e(TAG, "Could not create SQRLUri: " + ex.getMessage());
            return;
        }

        // Set the textview to display the URI
        friendlySiteNameTextView.setText(sqrlUri.getDisplayName());

        // Show the confirm/deny site buttons
        confirmDenySiteButtons = findViewById(R.id.ConfirmDenySiteButtons);
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
        this.mRequestFactory = new SQRLRequestFactory(this.sqrlUri);
        CreateAccountDialogFactory dialogFactory = new CreateAccountDialogFactory(this, getSupportFragmentManager());
        this.mAccountExistsTask = new AccountExistsTask(mRequestFactory, accountExistsTextView, dialogFactory, this);
        mAccountExistsTask.execute();
    }

    @Override
    public void abortIdentRequest() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void proceedWithIdentRequest() {
        IdentRequestTask identRequestTask = new IdentRequestTask(this.mRequestFactory, accountExistsTextView, this.mAccountExistsTask.getResponse());
        identRequestTask.execute();
    }
}
