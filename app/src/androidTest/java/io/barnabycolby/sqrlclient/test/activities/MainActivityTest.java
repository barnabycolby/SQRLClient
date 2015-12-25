package io.barnabycolby.sqrlclient.test.activities;

import android.test.*;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.content.res.Resources;
import android.view.View;

import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.R;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    //region TESTS

    public void testTapToProceedMessageShownWhenActivityOpenedNormally() {
        // Start the activity normally
        MainActivity mainActivity = getActivity();
        assertNotNull("mainActivity is null", mainActivity);

        assertTapToProceedMessageShownAndConfirmDenyButtonsNotVisible(mainActivity);
    }

    public void testShowErrorMessageForUnsupportedScheme() {
        // Create the URL with the unsupported scheme and start the activity
        String scheme = "lsx";
        String sqrlUri = scheme + "://sqrl-login.appspot.com/sqrl/auth?nut=7f8f1878ca7b4d3333daa8cfe3fe08f6";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);

        // Generate the expected text output
        Resources resources = mainActivity.getResources();
        String expected = resources.getString(R.string.invalid_link);

        assertThatTapToProceedTextViewMatchesString(mainActivity, expected);
    }

    public void testShowErrorMessageIfURIHasNoNut() {
        // Create the URL without the nut and start the activity
        String sqrlUri = "sqrl://sqrl-login.appspot.com/sqrl/auth";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);

        // Generate the expected text output
        Resources resources = mainActivity.getResources();
        String expected = resources.getString(R.string.invalid_link);

        assertThatTapToProceedTextViewMatchesString(mainActivity, expected);
    }

    public void testShowHostnameForValidURIWithoutFriendlyName() {
        String displayName = "sqrl-login.appspot.com";
        String sqrlUri = "qrl://" + displayName + ":80/sqrl/auth?nut=bf53570f7b9556c296963e8bd1578ec5";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);
        assertThatFriendlySiteNameMatchesString(mainActivity, displayName);
        assertThatConfirmDenySiteButtonsAreVisible(mainActivity, true);
    }

    public void testShowFriendlyNameForValidURIWithFriendlyName() {
        String sqrlUri = "sqrl://www.grc.com/sqrl?nut=mCwPTJWrbcBNMJKc76sI8w&sfn=R1JD";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);
        assertThatFriendlySiteNameMatchesString(mainActivity, "GRC");
        assertThatConfirmDenySiteButtonsAreVisible(mainActivity, true);
    }

    public void testTapToProceedMessageShownWhenSiteDenied() {
        // Prevent UI control from taking focus away from the test method
        setActivityInitialTouchMode(true);

        // Get to the point where the button will be shown
        String sqrlUri = "sqrl://www.grc.com/sqrl?nut=mCwPTJWrbcBNMJKc76sI8w&sfn=R1JD";
        MainActivity mainActivity = startActivityWithGivenURI(sqrlUri);
        assertThatConfirmDenySiteButtonsAreVisible(mainActivity, true);

        // Click the deny button
        View denySiteButton = mainActivity.findViewById(R.id.DenySiteButton);
        TouchUtils.clickView(this, denySiteButton);

        // Check that the user was redirected to the original view showing the 'tap to proceed' message
        assertTapToProceedMessageShownAndConfirmDenyButtonsNotVisible(mainActivity);
    }

    //endregion

    //region HELPERS

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
        assertEquals(View.VISIBLE, friendlySiteNameTextView.getVisibility());
    }

    public void assertThatTapToProceedTextViewMatchesString(MainActivity mainActivity, String expected) {
        // Get the URI text box and verify the uri matches
        TextView tapToProceedTextView = (TextView)mainActivity.findViewById(R.id.TapToProceedTextView);
        assertNotNull("TapToProceedTextView is null", tapToProceedTextView);
        assertEquals(expected, tapToProceedTextView.getText());
        assertEquals(View.VISIBLE, tapToProceedTextView.getVisibility());
    }

    private void assertThatConfirmDenySiteButtonsAreVisible(MainActivity mainActivity, boolean assertVisible) {
        View confirmDenySiteButtons = mainActivity.findViewById(R.id.ConfirmDenySiteButtons);
        assertNotNull("Could not find confirmDenySiteButtons.", confirmDenySiteButtons);

        if (assertVisible) {
            assertEquals(confirmDenySiteButtons.getVisibility(), View.VISIBLE);
        } else {
            MoreAsserts.assertNotEqual(confirmDenySiteButtons.getVisibility(), View.VISIBLE);
        }
    }

    private void assertTapToProceedMessageShownAndConfirmDenyButtonsNotVisible(MainActivity mainActivity) {
        // Check the tap to proceed message
        Resources resources = mainActivity.getResources();
        String noUri = resources.getString(R.string.no_uri);
        assertThatTapToProceedTextViewMatchesString(mainActivity, noUri);

        // Check that the friendly site name text view is not visible
        TextView friendlySiteNameTextView = (TextView)mainActivity.findViewById(R.id.FriendlySiteNameTextView);
        assertEquals(View.GONE, friendlySiteNameTextView.getVisibility());

        // Check that the confirm/deny buttons are hidden
        assertThatConfirmDenySiteButtonsAreVisible(mainActivity, false);
    }

    //endregion
}
