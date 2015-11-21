package io.barnabycolby.sqrlclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.net.Uri;
import android.content.res.Resources;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the uri from the data and the uri text box
        Intent intent = getIntent();
        Uri uri = intent.getData();
        TextView uriTextView = (TextView)findViewById(R.id.URITextView);
        if (uriTextView == null || uri == null) {
            return;
        }

        // Check the scheme of the URI is recognised
        Resources resources = getResources();
        String uriScheme = uri.getScheme().toLowerCase();
        if (!uriScheme.equals("sqrl") && !uriScheme.equals("qrl")) {
            String errorMessage = resources.getString(R.string.unknown_scheme, uriScheme);
            uriTextView.setText(errorMessage);
            return;
        }

        // Check the URI has a nut (query string parameter)
        String nut = uri.getQueryParameter("nut");
        if (nut == null) {
            String errorMessage = resources.getString(R.string.no_nut);
            uriTextView.setText(errorMessage);
            return;
        }

        uriTextView.setText(uri.toString());
    }
}
