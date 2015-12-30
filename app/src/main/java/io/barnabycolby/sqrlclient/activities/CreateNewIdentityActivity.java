package io.barnabycolby.sqrlclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.barnabycolby.sqrlclient.R;

/**
 * Activity used to allow the user to create a new SQRL identity.
 *
 * The activity harvests entropy from the camera and uses it to create a new SQRL identity.
 */
public class CreateNewIdentityActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_identity);
    }
}
