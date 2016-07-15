package io.barnabycolby.sqrlclient.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import io.barnabycolby.sqrlclient.activities.IdentityMustExistActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.dialogs.CreateAccountDialogFragment;
import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.helpers.ProceedAbortListener;
import io.barnabycolby.sqrlclient.helpers.SwappableTextView;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.tasks.AccountExistsTask;
import io.barnabycolby.sqrlclient.tasks.IdentRequestTask;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;

/**
 * Takes a SQRL URI as input and asks the user to confirm whether this is the site they wish to login to.
 * <p>
 * SQRL Uri is passed to this activity by clicking on a sqrl:// or qrl:// hyperlink (normally a QR code) in a browser.
 * The sites friendly name is displayed to the user with the choice of continuing with login or aborting.
 * If an account does not already exist, the user is asked whether they would like to create an account or not.
 * </p>
 */
public class ConfirmSiteNameActivity extends IdentityMustExistActivity {

    private static final String TAG = ConfirmSiteNameActivity.class.getName();
    private TextView mInformationTextView;
    private TextView mFriendlySiteNameTextView;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_site_name);

        // Get the uri from the data and the uri text box
        Intent intent = getIntent();
        this.mUri = intent.getData();
        if (this.mUri == null) {
            Log.e(TAG, "Uri passed via intent was null.");
            return;
        }

        // Store the uri in a SQRLUri so that we can query it more easily
        this.mInformationTextView = (TextView)findViewById(R.id.InformationTextView);
        SQRLUri sqrlUri;
        try {
            sqrlUri = new SQRLUri(this.mUri);
        } catch (SQRLException ex) {
            String errorMessage = this.getResources().getString(R.string.invalid_link);
            this.mInformationTextView.setText(errorMessage);
            Log.e(TAG, "Could not create SQRLUri: " + ex.getMessage());
            return;
        }

        // Display the friendly name
        this.mFriendlySiteNameTextView = (TextView)findViewById(R.id.FriendlySiteNameTextView);
        this.mFriendlySiteNameTextView.setText(sqrlUri.getDisplayName());
        this.mFriendlySiteNameTextView.setVisibility(View.VISIBLE);

        // Display the FQDN, but only if the friendly name is being shown
        // Otherwise, we would display the same URL twice
        if (sqrlUri.hasFriendlyName()) {
            TextView fqdnTextView = (TextView)findViewById(R.id.FQDNTextView);
            fqdnTextView.setText(sqrlUri.getHost());
            fqdnTextView.setVisibility(View.VISIBLE);
        }

        // Show the confirm/deny site buttons
        View confirmDenySiteButtons = findViewById(R.id.ConfirmDenySiteButtons);
        confirmDenySiteButtons.setVisibility(View.VISIBLE);
    }

    /**
     * Called when the deny site button is clicked.
     *
     * @param view  The view that was clicked.
     */
    public void denySite(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the confirm site button is clicked.
     *
     * @param view  The view that was clicked.
     */
    public void confirmSite(View view) {
        Intent intent = new Intent(this, EnterPasswordActivity.class);
        intent.setData(this.mUri);
        startActivity(intent);
    }
}
