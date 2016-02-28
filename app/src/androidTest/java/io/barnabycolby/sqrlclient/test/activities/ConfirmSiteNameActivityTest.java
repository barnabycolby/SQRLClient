package io.barnabycolby.sqrlclient.test.activities;

import android.test.*;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.content.res.Resources;
import android.view.View;

import io.barnabycolby.sqrlclient.activities.ConfirmSiteNameActivity;
import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.activities.NoIdentityActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.Helper;

public class ConfirmSiteNameActivityTest extends ActivityInstrumentationTestCase2<ConfirmSiteNameActivity> {

    public ConfirmSiteNameActivityTest() {
        super(ConfirmSiteNameActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        App.getSQRLIdentityManager().save("Gerald", new byte[32], "reg50NMg90!M0l5i");
    }

    @Override
    protected void tearDown() throws Exception {
        App.getSQRLIdentityManager().removeAllIdentities();
    }

    //region TESTS

    public void testShowErrorMessageForUnsupportedScheme() {
        // Create the URL with the unsupported scheme and start the activity
        String scheme = "lsx";
        String sqrlUri = scheme + "://sqrl-login.appspot.com/sqrl/auth?nut=7f8f1878ca7b4d3333daa8cfe3fe08f6";
        ConfirmSiteNameActivity activity = startActivityWithGivenURI(sqrlUri);

        // Generate the expected text output
        Resources resources = activity.getResources();
        String expected = resources.getString(R.string.invalid_link);

        assertThatTextViewMatchesString(activity, expected);
    }

    public void testShowErrorMessageIfURIHasNoNut() {
        // Create the URL without the nut and start the activity
        String sqrlUri = "sqrl://sqrl-login.appspot.com/sqrl/auth";
        ConfirmSiteNameActivity activity = startActivityWithGivenURI(sqrlUri);

        // Generate the expected text output
        Resources resources = activity.getResources();
        String expected = resources.getString(R.string.invalid_link);

        assertThatTextViewMatchesString(activity, expected);
    }

    public void testShowHostnameForValidURIWithoutFriendlyName() {
        String displayName = "sqrl-login.appspot.com";
        String sqrlUri = "qrl://" + displayName + ":80/sqrl/auth?nut=bf53570f7b9556c296963e8bd1578ec5";
        ConfirmSiteNameActivity activity = startActivityWithGivenURI(sqrlUri);
        assertThatFriendlySiteNameMatchesString(activity, displayName);
        assertThatConfirmDenySiteButtonsAreVisible(activity, true);
    }

    public void testShowFriendlyNameForValidURIWithFriendlyName() {
        String sqrlUri = "sqrl://www.grc.com/sqrl?nut=mCwPTJWrbcBNMJKc76sI8w&sfn=R1JD";
        ConfirmSiteNameActivity activity = startActivityWithGivenURI(sqrlUri);
        assertThatFriendlySiteNameMatchesString(activity, "GRC");
        assertThatConfirmDenySiteButtonsAreVisible(activity, true);
    }

    public void testRedirectedToNoIdentityActivityIfNoIdentitiesExist() throws Exception {
        // The setUp method has created an identity for us
        // As this test is a special case, we need to manually remove it
        App.getSQRLIdentityManager().removeAllIdentities();

        NoIdentityActivity activity = (NoIdentityActivity)Helper.monitorForActivity(NoIdentityActivity.class, 5000, new Lambda() {
            public void run() {
                String sqrlUri = "sqrl://www.grc.com/sqrl?nut=mCwPTJWrbcBNMJKc76sI8w&sfn=R1JD";
                startActivityWithGivenURI(sqrlUri);
            }
        });
        assertNotNull(activity);
    }

    //endregion

    //region HELPERS

    private ConfirmSiteNameActivity startActivityWithGivenURI(String uri) {
        // Create an Intent with the given URI
        Intent sqrlSchemeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        sqrlSchemeIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sqrlSchemeIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        // Start the activity by passing the intent
        setActivityIntent(sqrlSchemeIntent);
        ConfirmSiteNameActivity activity = getActivity();
        assertNotNull("activity is null", activity);

        return activity;
    }

    public void assertThatFriendlySiteNameMatchesString(ConfirmSiteNameActivity activity, String expected) {
        // Get the URI text box and verify the uri matches
        TextView friendlySiteNameTextView = (TextView)activity.findViewById(R.id.FriendlySiteNameTextView);
        assertNotNull("FriendlySiteNameTextView is null", friendlySiteNameTextView);
        assertEquals(expected, friendlySiteNameTextView.getText());
        assertEquals(View.VISIBLE, friendlySiteNameTextView.getVisibility());
    }

    public void assertThatTextViewMatchesString(ConfirmSiteNameActivity activity, String expected) {
        // Get the URI text box and verify the uri matches
        TextView informationTextView = (TextView)activity.findViewById(R.id.InformationTextView);
        assertNotNull("TextView is null", informationTextView);
        assertEquals(expected, informationTextView.getText());
        assertEquals(View.VISIBLE, informationTextView.getVisibility());
    }

    private void assertThatConfirmDenySiteButtonsAreVisible(ConfirmSiteNameActivity activity, boolean assertVisible) {
        View confirmDenySiteButtons = activity.findViewById(R.id.ConfirmDenySiteButtons);
        assertNotNull("Could not find confirmDenySiteButtons.", confirmDenySiteButtons);

        if (assertVisible) {
            assertEquals(confirmDenySiteButtons.getVisibility(), View.VISIBLE);
        } else {
            MoreAsserts.assertNotEqual(confirmDenySiteButtons.getVisibility(), View.VISIBLE);
        }
    }
    
    //endregion
}
