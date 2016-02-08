package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity displayed when the user enters the application, offering a menu of choices for interaction with the application.
 */
public class MainActivity extends AppCompatActivity {
    private SQRLIdentityManager mIdentityManager;
    private Spinner mIdentitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the identity manager
        this.mIdentityManager = App.getSQRLIdentityManager();

        // Initialise the spinner
        this.mIdentitySpinner = (Spinner)findViewById(R.id.IdentitySpinner);
        initialiseIdentitySpinner();
    }
    
    private void initialiseIdentitySpinner() {
        // Populate the spinner
        List<String> identityNames = this.mIdentityManager.getIdentityNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.identity_spinner_item, identityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mIdentitySpinner.setAdapter(adapter);
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
        } finally {
            // Make sure that the identity spinner is up to date
            initialiseIdentitySpinner();
        }

        if (identitySuccessfullyRemoved) {
            String message = this.getResources().getString(R.string.identity_deleted);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        // Reinitialise the identity spinner, the list may have changed
        initialiseIdentitySpinner();
    }
}
