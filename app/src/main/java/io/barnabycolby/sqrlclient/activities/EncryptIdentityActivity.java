package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;
import io.barnabycolby.sqrlclient.tasks.SaveIdentityTask;

/**
 * This activity encrypts a new identity.
 */
public class EncryptIdentityActivity extends AppCompatActivity implements PasswordCryptListener {

    private ProgressBar mEncryptProgressBar;
    private SaveIdentityTask mSaveIdentityTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_identity);

        // Store a reference to the progress bar
        this.mEncryptProgressBar = (ProgressBar)this.findViewById(R.id.EncryptProgressBar);

        // Extract the information passed via the intent
        Bundle extras = this.getIntent().getExtras();
        String identityName = extras.getString("identityName");
        byte[] masterKey = extras.getByteArray("masterKey");
        String password = extras.getString("password");
        boolean asyncTasksDisabled = extras.getBoolean("disableAsyncTasks", false);

        // Start the save identity async task
        if (!asyncTasksDisabled) {
            this.mSaveIdentityTask = new SaveIdentityTask(identityName, masterKey, password, this);
            this.mSaveIdentityTask.execute();
        }
    }

    @Override
    public void onPasswordCryptProgressUpdate(int progress) {
        this.mEncryptProgressBar.setProgress(progress);
    }

    @Override
    public void onPasswordCryptResult(boolean success) {
        if (success) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, this.mSaveIdentityTask.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
