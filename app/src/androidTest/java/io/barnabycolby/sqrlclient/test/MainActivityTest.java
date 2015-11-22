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

    public void testShowErrorMessageForUnsupportedScheme() {
        // Create the URL with the unsupported scheme and start the activity
        String scheme = "lsx";
        String sqrlUri = scheme + "://sqrl-login.appspot.com/sqrl/auth?nut=7f8f1878ca7b4d3333daa8cfe3fe08f6";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);

        // Generate the expected text output
        Resources resources = mainActivity.getResources();
        String expected = resources.getString(R.string.unknown_scheme, scheme);

        assertThatFriendlySiteNameMatchesString(mainActivity, expected);
    }

    public void testShowErrorMessageIfURIHasNoNut() {
        // Create the URL without the nut and start the activity
        String sqrlUri = "sqrl://sqrl-login.appspot.com/sqrl/auth";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);

        // Generate the expected text output
        Resources resources = mainActivity.getResources();
        String expected = resources.getString(R.string.no_nut);

        assertThatFriendlySiteNameMatchesString(mainActivity, expected);
    }

    public void testShowHostnameForValidURIWithoutFriendlyName() {
        String displayName = "sqrl-login.appspot.com";
        String sqrlUri = "qrl://" + displayName + ":80/sqrl/auth?nut=bf53570f7b9556c296963e8bd1578ec5";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);
        assertThatFriendlySiteNameMatchesString(mainActivity, displayName);
    }

    public void testShowFriendlyNameForValidURIWithFriendlyName() {
        String sqrlUri = "sqrl://www.grc.com/sqrl?nut=mCwPTJWrbcBNMJKc76sI8w&sfn=R1JD";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);
        assertThatFriendlySiteNameMatchesString(mainActivity, "GRC");
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

    public void assertThatFriendlySiteNameMatchesString(MainActivity mainActivity, String expected) {
        // Get the URI text box and verify the uri matches
        TextView friendlySiteNameTextView = (TextView)mainActivity.findViewById(R.id.FriendlySiteNameTextView);
        assertNotNull("FriendlySiteNameTextView is null", friendlySiteNameTextView);
        assertEquals(expected, friendlySiteNameTextView.getText());
    }

}
