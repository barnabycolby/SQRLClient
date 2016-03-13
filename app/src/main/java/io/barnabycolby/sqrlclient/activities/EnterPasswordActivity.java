package io.barnabycolby.sqrlclient.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.activities.fragments.EnterPasswordStateFragment;
import io.barnabycolby.sqrlclient.activities.StateFragmentActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.helpers.Helper;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.DecryptIdentityListener;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;
import io.barnabycolby.sqrlclient.tasks.DecryptIdentityTask;
import io.barnabycolby.sqrlclient.views.IdentitySpinner;

import java.security.GeneralSecurityException;

/**
 * This activity asks the user to enter the password for the selected identity, and then proceeds to verify it.
 */
public class EnterPasswordActivity extends StateFragmentActivity<EnterPasswordStateFragment> implements TextWatcher, DecryptIdentityListener {
    private static String TAG = EnterPasswordActivity.class.getName();

    // Allows the context of this activity to be accessed from within an inner class
    private Context mContext = this;

    // This variable provides support for tests that may require the async tasks to be disabled
    private boolean mAsyncTasksDisabled = false;
    private String mAsyncTasksDisabledKey = "asyncTasksDisabled";

    private Button mLoginButton;
    private EditText mPasswordEditText;
    private ProgressBar mVerifyProgressBar;
    private TextView mInformationTextView;
    private IdentitySpinner mIdentitySpinner;

    private boolean mLoginClicked = false;
    private String mLoginClickedKey = "loginClicked";
    private String mPasswordKey = "password";
    private SQRLUri mSQRLUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        // Store the uri given to us so that we can pass it on later
        Uri uri = this.getIntent().getData();
        if (uri == null) {
            String errorMessage = "Uri passed via intent was null.";
            Log.e(TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        // Wrap the uri in a SQRLUri object
        try {
            this.mSQRLUri = new SQRLUri(uri);
        } catch (SQRLException ex) {
            throw new RuntimeException(ex);
        }

        // Store a reference to any UI components required elsewhere
        this.mLoginButton = (Button)findViewById(R.id.LoginButton);
        this.mPasswordEditText = (EditText)findViewById(R.id.PasswordEditText);
        this.mVerifyProgressBar = (ProgressBar)findViewById(R.id.VerifyProgressBar);
        this.mInformationTextView = (TextView)findViewById(R.id.InformationTextView);
        this.mIdentitySpinner = (IdentitySpinner)findViewById(R.id.IdentitySpinner);

        // Add this class as a listener when the password changes
        this.mPasswordEditText.addTextChangedListener(this);

        super.initialiseFragment();
    }

    @Override
    protected EnterPasswordStateFragment initialise() {
        return new EnterPasswordStateFragment(this);
    }

    @Override
    protected void restore() {
        this.mStateFragment.getDecryptIdentityDetachableListener().attach(this);

        // Restore the password verification progress to the Progress Bar
        DecryptIdentityTask decryptIdentityTask = this.mStateFragment.getDecryptIdentityTask();
        if (decryptIdentityTask != null) {
            this.mVerifyProgressBar.setProgress(decryptIdentityTask.getProgress());
        }
    }

    @Override
    public void afterTextChanged(Editable password) {
        if (password.length() > 0) {
            this.mLoginButton.setEnabled(true);
        } else {
            this.mLoginButton.setEnabled(false);
        }
    }

    /**
     * Called when the login button is clicked.
     */
    public void onLoginButtonClicked(View view) {
        this.mLoginClicked = true;

        // Double check that the password field is not empty
        if (this.mPasswordEditText.getText().length() == 0) {
            String errorMessage = this.getResources().getString(R.string.password_is_blank);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        // Update the necessary UI components
        this.mLoginButton.setVisibility(View.GONE);
        this.mVerifyProgressBar.setVisibility(View.VISIBLE);
        this.mInformationTextView.setText(R.string.verifying_password);
        this.mPasswordEditText.setEnabled(false);
        this.mIdentitySpinner.setEnabled(false);

        // Start the identity decryption task
        // To support the testing of this activity, we need to check whether async tasks have been disabled
        if (!this.mAsyncTasksDisabled) {
            DecryptIdentityTask decryptIdentityTask = new DecryptIdentityTask(this.mStateFragment.getDecryptIdentityDetachableListener(), this.mSQRLUri);
            this.mStateFragment.setDecryptIdentityTask(decryptIdentityTask);
            String password = this.mPasswordEditText.getText().toString();
            decryptIdentityTask.execute(password);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(mLoginClickedKey, mLoginClicked);
        outState.putString(mPasswordKey, this.mPasswordEditText.getText().toString());
        outState.putBoolean(mAsyncTasksDisabledKey, this.mAsyncTasksDisabled);

        // Detach any listeners associated with this activity
        if (this.mStateFragment != null) {
            this.mStateFragment.getDecryptIdentityDetachableListener().detach();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        // Restore the async tasks disabled variable
        this.mAsyncTasksDisabled = inState.getBoolean(this.mAsyncTasksDisabledKey);

        // Restore the password
        String password = inState.getString(mPasswordKey);
        if (password != null) {
            this.mPasswordEditText.setText(password);
        }

        // Restore the rest of the UI
        this.mLoginClicked = inState.getBoolean(mLoginClickedKey);
        if (this.mLoginClicked) {
            onLoginButtonClicked(null);
        }
    }

    @Override
    public void onIdentityDecrypted(final SQRLIdentity identity) {
        try {
            Helper.runOnUIThread(this, new Lambda() {
                public void run() throws SQRLException, GeneralSecurityException {
                    if (identity == null) {
                        mLoginClicked = false;
                        mLoginButton.setVisibility(View.VISIBLE);
                        mVerifyProgressBar.setVisibility(View.GONE);
                        mInformationTextView.setText(R.string.incorrect_password);
                        mPasswordEditText.setEnabled(true);
                        mPasswordEditText.setText("");
                        mIdentitySpinner.setEnabled(true);
                    } else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        Bundle extras = new Bundle();
                        extras.putParcelable("sqrlIdentity", identity);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                }
            });
        } catch (Exception ex) {
            // The code inside run does not throw exceptions, so this should be impossible
            Log.wtf(TAG, "onPasswordCryptResult lambda threw an exception when it should have been impossible.");
        }
    }

    @Override
    public void onIdentityDecryptionProgressUpdate(int progress) {
        this.mVerifyProgressBar.setProgress(progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        Helper.checkIdentitiesExist(this);
    }

    // These methods are required by the TextWatcher interface, but we don't use them
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

    /**
     * Provides support for Espresso tests that may need to perform test steps during Async task execution.
     *
     * If async tasks are enabled, Espresso waits for them to finish before performing the next test step.
     */
    public void disableAsyncTasks() {
        this.mAsyncTasksDisabled = true;
    }
}
