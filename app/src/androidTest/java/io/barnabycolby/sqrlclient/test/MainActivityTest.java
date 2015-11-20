package io.barnabycolby.sqrlclient.test;

import android.test.ActivityInstrumentationTestCase2;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.MainActivity;
import io.barnabycolby.sqrlclient.R;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testSqrlScheme_setURITextBasedOnIntent() {
        // Create an Intent with a sqrl URI
        String sqrlUri = "sqrl://sqrl-login.appspot.com/sqrl/auth?nut=19d9d15d103aa22dfb59f4d6b39e98b2";
        Intent sqrlSchemeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sqrlUri));
        sqrlSchemeIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sqrlSchemeIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        // Start the activity by passing the intent
        setActivityIntent(sqrlSchemeIntent);
        MainActivity mainActivity = getActivity();
        assertNotNull("mainActivity is null", mainActivity);

        // Get the URI text box and verify the uri matches
        TextView uriTextView = (TextView)mainActivity.findViewById(R.id.URITextView);
        assertNotNull("uriTextView is null", uriTextView);
        assertEquals(sqrlUri, uriTextView.getText());
    }
}
