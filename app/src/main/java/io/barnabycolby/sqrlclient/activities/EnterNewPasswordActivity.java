package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeWrittenToDiskException;
import io.barnabycolby.sqrlclient.R;

/**
 * This activity asks the user to enter a new password, used for protecting an identity.
 */
public class EnterNewPasswordActivity extends AppCompatActivity implements TextWatcher {
    private String mIdentityName;
    private byte[] mMasterKey;
    private EditText mPasswordEditText;
    private EditText mSecondPasswordEditText;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_password);

        Intent intent = this.getIntent();
        this.mIdentityName = intent.getStringExtra("identityName");
        this.mMasterKey = intent.getByteArrayExtra("masterKey");
        if (this.mIdentityName == null || this.mMasterKey == null) {
            throw new RuntimeException(new IllegalArgumentException("Missing required intent extras."));
        }

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

    public void onNextClicked(View view) throws IdentitiesCouldNotBeLoadedException, IdentityCouldNotBeWrittenToDiskException {
        // Attempt to save the new identity, displaying a dialog if it already exists
        try {
            App.getSQRLIdentityManager().save(this.mIdentityName, this.mMasterKey);
        } catch (IdentityAlreadyExistsException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();

            // We only finish the activity if an identity has been successfully created
            // Otherwise the user would have to regenerate the entropy
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
