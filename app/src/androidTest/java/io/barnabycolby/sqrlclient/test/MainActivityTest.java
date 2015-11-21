package io.barnabycolby.sqrlclient.test;

import android.test.ActivityInstrumentationTestCase2;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.content.res.Resources;

import io.barnabycolby.sqrlclient.MainActivity;
import io.barnabycolby.sqrlclient.R;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    private void startActivityWithGivenSchemeAndTestTextViewMatchesURI(String scheme) {
        String sqrlUri = scheme + "://sqrl-login.appspot.com/sqrl/auth?nut=19d9d15d103aa22dfb59f4d6b39e98b2";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);

        // Get the URI text box and verify the uri matches
        TextView uriTextView = (TextView)mainActivity.findViewById(R.id.URITextView);
        assertNotNull("uriTextView is null", uriTextView);
        assertEquals(sqrlUri, uriTextView.getText());

        // Close the activity to prevent interference with future tests
        mainActivity.finish();
    }

    private MainActivity startActivityWithGivenURI(String uri) {
        // Create an Intent with the given URI
        Intent sqrlSchemeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        sqrlSchemeIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sqrlSchemeIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        // Start the activity by passing the intent
        setActivityIntent(sqrlSchemeIntent);
        MainActivity mainActivity = getActivity();
        assertNotNull("mainActivity is null", mainActivity);

        return mainActivity;
    }

    public void testsetURITextViewToLowercaseSQRLURI() {
        startActivityWithGivenSchemeAndTestTextViewMatchesURI("sqrl");
    }

    public void testsetURITextViewToLowercaseQRLURI() {
        startActivityWithGivenSchemeAndTestTextViewMatchesURI("qrl");
    }

    public void testsetURITextViewToUppercaseSQRLURI() {
        startActivityWithGivenSchemeAndTestTextViewMatchesURI("SQRL");
    }
    
    public void testsetURITextViewToUppercaseQRLURI() {
        startActivityWithGivenSchemeAndTestTextViewMatchesURI("QRL");
    }

    public void testSqrlScheme_showErrorMessageForUnsupportedScheme() {
        // Create the URL with the unsupported scheme and start the activity
        String scheme = "lsx";
        String sqrlUri = scheme + "://sqrl-login.appspot.com/sqrl/auth?nut=7f8f1878ca7b4d3333daa8cfe3fe08f6";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);

        // Generate the expected text output
        Resources resources = mainActivity.getResources();
        String expected = resources.getString(R.string.unknown_scheme, scheme);

        // Get the URI text box and verify the uri matches
        TextView uriTextView = (TextView)mainActivity.findViewById(R.id.URITextView);
        assertNotNull("uriTextView is null", uriTextView);
        assertEquals(expected, uriTextView.getText());
    }
}
