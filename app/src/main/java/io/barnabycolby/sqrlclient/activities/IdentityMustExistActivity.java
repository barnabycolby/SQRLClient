package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import io.barnabycolby.sqrlclient.App;

/**
 * Activities that should only work when identities already exist should extend this activity.
 *
 * When the activity is first created or is resumed, a call is made to check whether any identites exist.
 * If not, then the application is redirected to the NoIdentity activity.
 */
public class IdentityMustExistActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        checkIdentitiesExist();
    }

    /**
     * Checks whether any identities still exist, starting the NoIdentity activity if not.
     *
     * @return True if some identities exist, and false if not. Note that false also indicates an intent has been started for the NoIdentityActivity.
     */
    public boolean checkIdentitiesExist() {
        // Check whether there are actually some identities
        if (!App.getSQRLIdentityManager().containsIdentities()) {
            Intent intent = new Intent(this, NoIdentityActivity.class);
            startActivity(intent);
            return false;
        }

        return true;
    }
}
