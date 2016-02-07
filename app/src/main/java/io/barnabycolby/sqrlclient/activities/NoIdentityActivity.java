package io.barnabycolby.sqrlclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.barnabycolby.sqrlclient.R;

/**
 * Activity displayed when no SQRL identities are associated with the application, for example, the first time the application is launched.
 */
public class NoIdentityActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_identity);
    }
}
