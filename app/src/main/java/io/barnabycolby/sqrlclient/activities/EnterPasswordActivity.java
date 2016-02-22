package io.barnabycolby.sqrlclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import io.barnabycolby.sqrlclient.R;

/**
 * This activity asks the user to enter the password for the selected identity, and then proceeds to verify it.
 */
public class EnterPasswordActivity extends AppCompatActivity implements TextWatcher {
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        // Store a reference to any UI components required elsewhere
        this.mLoginButton = (Button)findViewById(R.id.LoginButton);

        // Add this class as a listener when the password changes
        EditText passwordEditText = (EditText)findViewById(R.id.PasswordEditText);
        passwordEditText.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable password) {
        if (password.length() > 0) {
            this.mLoginButton.setEnabled(true);
        } else {
            this.mLoginButton.setEnabled(false);
        }
    }

    // These methods are required by the TextWatcher interface, but we don't use them
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
