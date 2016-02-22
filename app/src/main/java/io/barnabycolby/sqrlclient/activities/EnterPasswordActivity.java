package io.barnabycolby.sqrlclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.R;

/**
 * This activity asks the user to enter the password for the selected identity, and then proceeds to verify it.
 */
public class EnterPasswordActivity extends AppCompatActivity implements TextWatcher {
    private Button mLoginButton;
    private EditText mPasswordEditText;
    private ProgressBar mVerifyProgressBar;
    private TextView mInformationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        // Store a reference to any UI components required elsewhere
        this.mLoginButton = (Button)findViewById(R.id.LoginButton);
        this.mPasswordEditText = (EditText)findViewById(R.id.PasswordEditText);
        this.mVerifyProgressBar = (ProgressBar)findViewById(R.id.VerifyProgressBar);
        this.mInformationTextView = (TextView)findViewById(R.id.InformationTextView);

        // Add this class as a listener when the password changes
        this.mPasswordEditText.addTextChangedListener(this);
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
    }

    // These methods are required by the TextWatcher interface, but we don't use them
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
