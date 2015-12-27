package io.barnabycolby.sqrlclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import io.barnabycolby.sqrlclient.R;

/**
 * This activity displays the possible login choices a user has.
 *
 * A button is displayed allowing the user to scan a QR code, as well as a message explaining how to login on this device.
 */
public class LoginChoicesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choices);
    }

    public void login(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Uri uri = Uri.parse(scanResult.getContents());
            Intent confirmSiteNameIntent = new Intent(this, ConfirmSiteNameActivity.class);
            confirmSiteNameIntent.setData(uri);
            startActivity(confirmSiteNameIntent);
        }
    }
}
