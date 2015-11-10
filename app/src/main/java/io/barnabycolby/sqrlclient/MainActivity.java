package io.barnabycolby.sqrlclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        TextView uriTextView = (TextView)findViewById(R.id.URITextView);
        if (uriTextView == null || uri == null) {
            return;
        }
        uriTextView.setText(uri.toString());
    }
}
