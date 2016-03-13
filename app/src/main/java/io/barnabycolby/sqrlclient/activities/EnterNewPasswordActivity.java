package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeWrittenToDiskException;
import io.barnabycolby.sqrlclient.R;

import java.security.GeneralSecurityException;

/**
 * This activity asks the user to enter a new password, used for protecting an identity.
 */
public class EnterNewPasswordActivity extends AppCompatActivity implements TextWatcher {
    private String mIdentityName;
    private byte[] mMasterKey;
    private EditText mPasswordEditText;
    private EditText mSecondPasswordEditText;
    private Button mNextButton;
    private ProgressBar mPasswordStrengthMeter;

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
        this.mPasswordStrengthMeter = (ProgressBar)findViewById(R.id.PasswordStrengthMeter);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String password = this.mPasswordEditText.getText().toString();
        String secondPassword = this.mSecondPasswordEditText.getText().toString();
        updatePasswordStrengthMeter();

        if (secondPassword.equals(password) && this.mPasswordStrengthMeter.getProgress() >= 100) {
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

    public void onNextClicked(View view) throws IdentitiesCouldNotBeLoadedException, IdentityCouldNotBeWrittenToDiskException, GeneralSecurityException {
        Intent intent = new Intent(this, EncryptIdentityActivity.class);
        Bundle extras = new Bundle();
        extras.putString("identityName", this.mIdentityName);
        extras.putByteArray("masterKey", this.mMasterKey);
        extras.putString("password", this.mPasswordEditText.getText().toString());
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     * Character classes used for the password strength meter algorithm.
     */
    private enum CharacterClass {
        NONE,
        LOWERTHAN128,
        DIGIT,
        LOWERCASE,
        GREATERCASE,
        GREATERTHAN128
    };

    /**
     * Updates the password strength meter to reflect the strength of the password.
     *
     * Based on a password strength algorithm described in episode 468 of Security Now: https://www.grc.com/sn/sn-468.htm
     */
    private void updatePasswordStrengthMeter() {
        String password = this.mPasswordEditText.getText().toString();
        int numberOfCharacters = password.length();

        // Iterate over string characters
        // Number of class transitions starts at -1 as an easy way to make the first transition from NONE not count
        int numberOfClassTransitions = -1;
        String uniqueCharacters = "";
        CharacterClass previousClass = CharacterClass.NONE;
        for (char c : password.toCharArray()) {
            // Count number of unique characters
            if (!uniqueCharacters.contains(String.valueOf(c))) {
                uniqueCharacters += c;
            }

            // See if this is a class transition
            CharacterClass characterClass = CharacterClass.NONE;
            int code = (int)c;
            if (code < 58 && code > 47) {
                characterClass = CharacterClass.DIGIT;
            } else if (code < 91 && code > 64) {
                characterClass = CharacterClass.GREATERCASE;
            } else if (code < 123 && code > 96) {
                characterClass = CharacterClass.LOWERCASE;
            } else if (code < 128) {
                characterClass = CharacterClass.LOWERTHAN128;
            } else if (code > 127) {
                characterClass = CharacterClass.GREATERTHAN128;
            }
            if (characterClass != previousClass) {
                numberOfClassTransitions += 1;
            }
            previousClass = characterClass;
        }
        int numberOfUniqueCharacters = uniqueCharacters.length();

        int passwordStrengthNumerator = numberOfCharacters + numberOfUniqueCharacters + (2 * numberOfClassTransitions);
        int passwordStrength = (int)((((float)passwordStrengthNumerator) / 30) * 100);
        this.mPasswordStrengthMeter.setProgress(passwordStrength);
    }
}
