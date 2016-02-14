package io.barnabycolby.sqrlclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.barnabycolby.sqrlclient.R;

/**
 * This activity asks the user to enter a new password, used for protecting an identity.
 */
public class EnterNewPasswordActivity extends AppCompatActivity implements TextWatcher {
    private EditText mPasswordEditText;
    private EditText mSecondPasswordEditText;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_password);

        this.mPasswordEditText = (EditText)findViewById(R.id.PasswordEditText);
        this.mPasswordEditText.addTextChangedListener(this);
        this.mSecondPasswordEditText = (EditText)findViewById(R.id.SecondPasswordEditText);
        this.mSecondPasswordEditText.addTextChangedListener(this);
        this.mNextButton = (Button)findViewById(R.id.NextButton);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String password = this.mPasswordEditText.getText().toString();
        String secondPassword = this.mSecondPasswordEditText.getText().toString();
        if (secondPassword.equals(password)) {
            this.mNextButton.setEnabled(true);
        } else {
            this.mNextButton.setEnabled(false);
        }
    }

    // We only need to use the afterTextChanged method, so we can ignore these two
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    public void onNextClicked(View view) {
        this.finish();
    }
}
