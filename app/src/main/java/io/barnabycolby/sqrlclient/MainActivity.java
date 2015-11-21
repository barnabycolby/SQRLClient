package io.barnabycolby.sqrlclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.net.Uri;
import android.content.res.Resources;

public class MainActivity extends AppCompatActivity {

    private SQRLUri sqrlUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the uri from the data and the uri text box
        Intent intent = getIntent();
        Uri uri = intent.getData();
        TextView uriTextView = (TextView)findViewById(R.id.URITextView);
        if (uriTextView == null || uri == null) {
            return;
        }

        // Store the uri in a SQRLUri so that we can query it more easily
        Resources resources = getResources();
        String errorMessage;
        try {
            sqrlUri = new SQRLUri(uri);
        } catch (UnknownSchemeException ex) {
            errorMessage = resources.getString(R.string.unknown_scheme, ex.getScheme());
            uriTextView.setText(errorMessage);
            return;
        } catch (NoNutException ex) {
            errorMessage = resources.getString(R.string.no_nut);
            uriTextView.setText(errorMessage);
            return;
        }

        // Set the textview to display the URI
        uriTextView.setText(uri.toString());
    }
}
