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
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.activities.StateFragmentActivity;
import io.barnabycolby.sqrlclient.dialogs.CreateAccountDialogFragment;
import io.barnabycolby.sqrlclient.exceptions.*;
import io.barnabycolby.sqrlclient.helpers.Helper;
import io.barnabycolby.sqrlclient.helpers.IdentRequestListener;
import io.barnabycolby.sqrlclient.helpers.ProceedAbortListener;
import io.barnabycolby.sqrlclient.helpers.SwappableTextView;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.tasks.AccountExistsTask;
import io.barnabycolby.sqrlclient.tasks.IdentRequestTask;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;

/**
 * Performs the login sequence to a given site.
 */
public class LoginActivity extends StateFragmentActivity<LoginStateFragment> implements IdentRequestListener {
    private static final String TAG = LoginActivity.class.getName();

    private boolean mInitialiseSucceeded = true;

    private SwappableTextView informationTextView;
    private TextView friendlySiteNameTextView;

    private AccountExistsTask mAccountExistsTask;
    private IdentRequestTask mIdentRequestTask;

    private boolean mDoneButtonVisible = false;
    private String mDoneButtonVisibleKey = "mDoneButtonVisible";

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
    }

    @Override
    protected LoginStateFragment initialise() {
        // Get the SQRLIdentity from the intent
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            Log.e(TAG, "A Bundle was not passed to the login activity.");
            this.mInitialiseSucceeded = false;
            return null;
        }
        SQRLIdentity identity = bundle.getParcelable("sqrlIdentity");
        if (identity == null) {
            Log.e(TAG, "A SQRLIdentity object was not passed to the login activity.");
            this.mInitialiseSucceeded = false;
            return null;
        }

        // Store the uri in a SQRLUri so that we can query it more easily
        TextView rawInformationTextView = (TextView)findViewById(R.id.InformationTextView);
        this.informationTextView = new SwappableTextView(rawInformationTextView);

        // Create the SQRLRequestFactory used to generate requests
        SQRLRequestFactory requestFactory = new SQRLRequestFactory(identity);

        // Retrieve the friendly name
        String displayName = identity.getSQRLUri().getDisplayName();

        // Create the state fragment to store the state
        this.mInitialiseSucceeded = true;
        LoginStateFragment stateFragment =  new LoginStateFragment(this.informationTextView, identity, requestFactory, this.getAccountExistsListener(), this.getDialogListener(), this, displayName);

        // Start the login procedure
        this.mAccountExistsTask = new AccountExistsTask(requestFactory, this.informationTextView, stateFragment.getAccountExistsDetachableListener());
        this.mAccountExistsTask.execute();

        return stateFragment;
    }

    @Override
    protected void restore() {
        // Update the swappable text views underlying text view
        TextView rawInformationTextView = (TextView)findViewById(R.id.InformationTextView);
        this.informationTextView = this.mStateFragment.getInformationTextView();
        this.informationTextView.setTextView(rawInformationTextView);

        // Reattach the listeners used for callbacks
        this.mStateFragment.getAccountExistsDetachableListener().attach(this.getAccountExistsListener());
        this.mStateFragment.getDialogDetachableListener().attach(this.getDialogListener());
        this.mStateFragment.getIdentRequestDetachableListener().attach(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Detach any listeners associated with this activity
        if (this.mStateFragment != null) {
            this.mStateFragment.getAccountExistsDetachableListener().detach();
            this.mStateFragment.getDialogDetachableListener().detach();
            this.mStateFragment.getIdentRequestDetachableListener().detach();
        }

        // Store the state of the done button
        outState.putBoolean(this.mDoneButtonVisibleKey, this.mDoneButtonVisible);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        // Restore the state of the done button
        View doneButton = this.findViewById(R.id.DoneButton);
        this.mDoneButtonVisible = inState.getBoolean(this.mDoneButtonVisibleKey);
        if (this.mDoneButtonVisible) {
            doneButton.setVisibility(View.VISIBLE);
        } else {
            doneButton.setVisibility(View.GONE);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Called if the users account already exists, or they have chosen to create a new account when presented with a dialog offering the choice.
     */
    public void proceedWithIdentRequest() {
        this.mIdentRequestTask = new IdentRequestTask(this.mStateFragment.getRequestFactory(), informationTextView, this.mStateFragment.getIdentRequestDetachableListener());
        this.mIdentRequestTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // We need to cancel the async tasks before exiting, otherwise they may behave badly
        if (this.isFinishing()) {
            if (this.mAccountExistsTask != null && this.mAccountExistsTask.getStatus() != AsyncTask.Status.FINISHED) {
                this.mAccountExistsTask.cancel(true);
            }
            if (this.mIdentRequestTask != null && this.mIdentRequestTask.getStatus() != AsyncTask.Status.FINISHED) {
                this.mIdentRequestTask.cancel(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Helper.checkIdentitiesExist(this);
    }

    @Override
    public void onIdentRequestFinished() {
        // Show the done button
        View doneButton = this.findViewById(R.id.DoneButton);
        doneButton.setVisibility(View.VISIBLE);
        this.mDoneButtonVisible = true;
    }

    /**
     * Called when the done button is clicked.
     */
    public void onDoneClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
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
