package io.barnabycolby.sqrlclient.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import io.barnabycolby.sqrlclient.activities.fragments.LoginStateFragment;
import io.barnabycolby.sqrlclient.activities.StateFragmentActivity;
import io.barnabycolby.sqrlclient.dialogs.CreateAccountDialogFragment;
import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.helpers.Helper;
import io.barnabycolby.sqrlclient.helpers.ProceedAbortListener;
import io.barnabycolby.sqrlclient.helpers.SwappableTextView;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.tasks.AccountExistsTask;
import io.barnabycolby.sqrlclient.tasks.IdentRequestTask;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;

/**
 * Performs the login sequence to a given site.
 */
public class LoginActivity extends StateFragmentActivity<LoginStateFragment> {
    private static final String TAG = LoginActivity.class.getName();

    private boolean mInitialiseSucceeded = true;

    private SwappableTextView informationTextView;
    private TextView friendlySiteNameTextView;
    private SQRLUri sqrlUri;

    private AccountExistsTask mAccountExistsTask;
    private IdentRequestTask mIdentRequestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        super.initialiseFragment();

        // Check for error
        if (!this.mInitialiseSucceeded) {
            return;
        }

        // Set the textview to display the URI
        this.friendlySiteNameTextView = (TextView)findViewById(R.id.FriendlySiteNameTextView);
        friendlySiteNameTextView.setText(this.mStateFragment.getDisplayName());
        friendlySiteNameTextView.setVisibility(View.VISIBLE);

        // Start the login procedure
        this.mAccountExistsTask = new AccountExistsTask(this.mStateFragment.getRequestFactory(), informationTextView, this.mStateFragment.getAccountExistsDetachableListener());
        this.mAccountExistsTask.execute();
    }

    @Override
    protected LoginStateFragment initialise() {
        // Get the uri from the data and the uri text box
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri == null) {
            Log.e(TAG, "Uri passed via intent was null.");
            this.mInitialiseSucceeded = false;
            return null;
        }

        // Store the uri in a SQRLUri so that we can query it more easily
        TextView rawInformationTextView = (TextView)findViewById(R.id.InformationTextView);
        this.informationTextView = new SwappableTextView(rawInformationTextView);
        try {
            sqrlUri = new SQRLUri(uri);
        } catch (SQRLException ex) {
            String errorMessage = this.getResources().getString(R.string.invalid_link);
            informationTextView.setText(errorMessage);
            Log.e(TAG, "Could not create SQRLUri: " + ex.getMessage());
            this.mInitialiseSucceeded = false;
            return null;
        }

        // Create the SQRLRequestFactory used to generate requests
        SQRLRequestFactory requestFactory = new SQRLRequestFactory(this.sqrlUri);

        // Retrieve the friendly name
        String displayName = sqrlUri.getDisplayName();

        // Create the state fragment to store the state
        this.mInitialiseSucceeded = true;
        return new LoginStateFragment(this.informationTextView, this.sqrlUri, requestFactory, this.getAccountExistsListener(), this.getDialogListener(), displayName);
    }

    @Override
    protected void restore() {
        // Update the swappable text views underlying text view
        TextView rawInformationTextView = (TextView)findViewById(R.id.InformationTextView);
        this.informationTextView = this.mStateFragment.getInformationTextView();
        this.informationTextView.setTextView(rawInformationTextView);

        // Retrieve the SQRL Uri
        this.sqrlUri = this.mStateFragment.getSQRLUri();

        // Reattach the listeners used for callbacks
        this.mStateFragment.getAccountExistsDetachableListener().attach(this.getAccountExistsListener());
        this.mStateFragment.getDialogDetachableListener().attach(this.getDialogListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Detach any listeners associated with this activity
        if (this.mStateFragment != null) {
            this.mStateFragment.getAccountExistsDetachableListener().detach();
            this.mStateFragment.getDialogDetachableListener().detach();
        }
    }

    /**
     * Called when the account exists task has determined that the account already exists.
     */
    public void onAccountAlreadyExists() {
        proceedWithIdentRequest();
    }

    /**
     * Called when the account exists task has determined that the account does not already exist.
     */
    public void onAccountDoesNotAlreadyExist() {
        // We need to ask the user if they would like to create an account or not
        CreateAccountDialogFragment dialog = new CreateAccountDialogFragment(this.mStateFragment.getDialogDetachableListener());
        dialog.show(this.getSupportFragmentManager(), "createAccount");
    }

    /**
     * Called if the user chooses not to create a new account, when presented with a dialog offering the choice.
     */
    public void abortIdentRequest() {
        Intent intent = new Intent(this, LoginChoicesActivity.class);
        startActivity(intent);
    }

    /**
     * Called if the users account already exists, or they have chosen to create a new account when presented with a dialog offering the choice.
     */
    public void proceedWithIdentRequest() {
        this.mIdentRequestTask = new IdentRequestTask(this.mStateFragment.getRequestFactory(), informationTextView);
        this.mIdentRequestTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // We need to cancel the async tasks before exiting, otherwise they may behave badly
        if (this.mAccountExistsTask != null && this.mAccountExistsTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mAccountExistsTask.cancel(true);
        }
        if (this.mIdentRequestTask != null && this.mIdentRequestTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mIdentRequestTask.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Helper.checkIdentitiesExist(this);
    }

    /**
     * Creates a dialog listener.
     *
     * The main purpose is to prevent the calling code from becoming ugly and unreadable.
     */
    private ProceedAbortListener getDialogListener() {
        return new ProceedAbortListener() {
            @Override
            public void proceed() {
                proceedWithIdentRequest();
            }

            @Override
            public void abort() {
                abortIdentRequest();
            }
        };
    }

    /**
     * Creates an account exists listener.
     *
     * The main purpose is to prevent the calling code from becoming ugly and unreadable.
     */
    private ProceedAbortListener getAccountExistsListener() {
        return new ProceedAbortListener() {
            @Override
            public void proceed() {
                onAccountAlreadyExists();
            }

            @Override
            public void abort() {
                onAccountDoesNotAlreadyExist();
            }
        };
    }
}
