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
 * Takes a SQRL URI as input and asks the user to confirm whether this is the site they wish to login to.
 * <p>
 * SQRL Uri is passed to this activity by clicking on a sqrl:// or qrl:// hyperlink (normally a QR code) in a browser.
 * The sites friendly name is displayed to the user with the choice of continuing with login or aborting.
 * If an account does not already exist, the user is asked whether they would like to create an account or not.
 * </p>
 */
public class ConfirmSiteNameActivity extends AppCompatActivity implements IdentRequestListener {

    private static final String TAG = ConfirmSiteNameActivity.class.getName();
    private TextView informationTextView;
    private TextView friendlySiteNameTextView;
    private SQRLUri sqrlUri;
    private View confirmDenySiteButtons;
    private Resources resources;
    private SQRLRequestFactory mRequestFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_site_name);

        // Get the uri from the data and the uri text box
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri == null) {
            Log.e(TAG, "Uri passed via intent was null.");
            return;
        }

        // Store the uri in a SQRLUri so that we can query it more easily
        this.resources = getResources();
        this.informationTextView = (TextView)findViewById(R.id.InformationTextView);
        this.informationTextView.setText("");
        try {
            sqrlUri = new SQRLUri(uri);
        } catch (SQRLException ex) {
            String errorMessage = resources.getString(R.string.invalid_link);
            informationTextView.setText(errorMessage);
            Log.e(TAG, "Could not create SQRLUri: " + ex.getMessage());
            return;
        }

        // Set the textview to display the URI
        this.friendlySiteNameTextView = (TextView)findViewById(R.id.FriendlySiteNameTextView);
        friendlySiteNameTextView.setText(sqrlUri.getDisplayName());
        friendlySiteNameTextView.setVisibility(View.VISIBLE);

        // Show the confirm/deny site buttons
        confirmDenySiteButtons = findViewById(R.id.ConfirmDenySiteButtons);
        confirmDenySiteButtons.setVisibility(View.VISIBLE);
    }

    /**
     * Called when the deny site button is clicked.
     */
    public void denySite(View view) {
        abortIdentRequest();
    }

    /**
     * Called when the confirm site button is clicked.
     */
    public void confirmSite(View view) {
        this.mRequestFactory = new SQRLRequestFactory(this.sqrlUri);
        CreateAccountDialogFactory dialogFactory = new CreateAccountDialogFactory(this, getSupportFragmentManager());
        AccountExistsTask accountExistsTask = new AccountExistsTask(mRequestFactory, informationTextView, dialogFactory, this);
        accountExistsTask.execute();
    }

    @Override
    public void abortIdentRequest() {
        Intent intent = new Intent(this, LoginChoicesActivity.class);
        startActivity(intent);
    }

    @Override
    public void proceedWithIdentRequest() {
        IdentRequestTask identRequestTask = new IdentRequestTask(this.mRequestFactory, informationTextView);
        identRequestTask.execute();
    }
}
