package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity displayed when the user enters the application, offering a menu of choices for interaction with the application.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise the identity spinner
        Spinner identitySpinner = (Spinner)findViewById(R.id.IdentitySpinner);
        List<String> identityNames = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, identityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        identitySpinner.setAdapter(adapter);
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
}
