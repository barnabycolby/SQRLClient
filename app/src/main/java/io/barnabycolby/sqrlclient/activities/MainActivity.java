package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.IdentityMustExistActivity;
import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityDoesNotExistException;
import io.barnabycolby.sqrlclient.exceptions.SQRLException;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity displayed when the user enters the application, offering a menu of choices for interaction with the application.
 */
public class MainActivity extends IdentityMustExistActivity implements AdapterView.OnItemSelectedListener {
    private static String TAG = MainActivity.class.getName();

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
        this.mIdentitySpinner.setOnItemSelectedListener(this);
        initialiseIdentitySpinner();
    }
    
    private void initialiseIdentitySpinner() {
        // Populate the spinner
        List<String> identityNames = this.mIdentityManager.getIdentityNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.identity_spinner_item, identityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mIdentitySpinner.setAdapter(adapter);

        // Set the currently selected identity
        if (identityNames.size() > 0) {
            // Check whether a current identity has already been selected
            String currentIdentityName = this.mIdentityManager.getCurrentIdentityName();
            if (currentIdentityName != null && identityNames.contains(currentIdentityName)) {
                this.mIdentitySpinner.setSelection(adapter.getPosition(currentIdentityName));
            } else {
                try {
                    this.mIdentityManager.setCurrentIdentity((String)this.mIdentitySpinner.getSelectedItem());
                } catch (IdentityDoesNotExistException ex) {
                    Log.wtf(TAG, "Identity name taken from SQRLIdentityManager.getIdentityNames() list does not exist according to setCurrentIdentity.");
                    throw new RuntimeException(ex);
                }
            }
        }
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

    /**
     * Called when an item in the identity spinner is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedIdentityName = (String)parent.getItemAtPosition(position);
        try {
            this.mIdentityManager.setCurrentIdentity(selectedIdentityName);
        } catch (IdentityDoesNotExistException ex) {
            Log.wtf(TAG, "Identity selected from identity spinner does not exist.");
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
