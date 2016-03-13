package io.barnabycolby.sqrlclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.barnabycolby.sqrlclient.R;

/**
 * This activity encrypts a new identity.
 */
public class EncryptIdentityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_identity);
    }
}
