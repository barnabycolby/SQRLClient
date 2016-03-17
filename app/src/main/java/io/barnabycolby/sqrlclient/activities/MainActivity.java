package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import io.barnabycolby.sqrlclient.activities.ConfirmSiteNameActivity;
import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.helpers.Helper;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;
import io.barnabycolby.sqrlclient.views.IdentitySpinner;

/**
 * Activity displayed when the user enters the application, offering a menu of choices for interaction with the application.
 */
public class MainActivity extends AppCompatActivity {
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
        // Start the QR code scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Check that we actually scanned a URL
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null || scanResult.getContents() == null) {
            return;
        }
        Uri uri = Uri.parse(scanResult.getContents());

        // Start the login process, passing it the url
        Intent confirmSiteNameIntent = new Intent(this, ConfirmSiteNameActivity.class);
        confirmSiteNameIntent.setData(uri);
        startActivity(confirmSiteNameIntent);
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

        if (!Helper.checkIdentitiesExist(this)) {
            // Because checkIdentitiesExist returned false, we must be about to move to the NoIdentityActivity
            // So we don't care about updating the UI components on this activity
            return;
        }
        
        this.mIdentitySpinner.repopulate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Helper.checkIdentitiesExist(this);
    }
}
