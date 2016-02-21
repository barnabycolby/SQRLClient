package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.IdentityMustExistActivity;
import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;
import io.barnabycolby.sqrlclient.views.IdentitySpinner;

/**
 * Activity displayed when the user enters the application, offering a menu of choices for interaction with the application.
 */
public class MainActivity extends IdentityMustExistActivity {
    private static String TAG = MainActivity.class.getName();

    private SQRLIdentityManager mIdentityManager;
    private IdentitySpinner mIdentitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the identity manager
        this.mIdentityManager = App.getSQRLIdentityManager();

        // Initialise the spinner
        this.mIdentitySpinner = (IdentitySpinner)findViewById(R.id.IdentitySpinner);
    }

    /**
     * Called when the login button is pressed.
     */
    public void login(View view) {
        Intent intent = new Intent(this, LoginChoicesActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the create new identity button is pressed.
     */
    public void createNewIdentity(View view) {
        Intent intent = new Intent(this, CreateNewIdentityActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the delete identity button is pressed.
     */
    public void deleteIdentity(View view) {
        String currentlySelectedIdentityName = (String)this.mIdentitySpinner.getSelectedItem();
        boolean identitySuccessfullyRemoved = false;
        try {
            this.mIdentityManager.removeIdentity(currentlySelectedIdentityName);
            identitySuccessfullyRemoved = true;
        } catch (SQRLException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Display a toast message if successful
        if (identitySuccessfullyRemoved) {
            String message = this.getResources().getString(R.string.identity_deleted);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        if (!this.checkIdentitiesExist()) {
            // Because checkIdentitiesExist returned false, we must be about to move to the NoIdentityActivity
            // So we don't care about updating the UI components on this activity
            return;
        }
        
        this.mIdentitySpinner.repopulate(this);
    }
}
