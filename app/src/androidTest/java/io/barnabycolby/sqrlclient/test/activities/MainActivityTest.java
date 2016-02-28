package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.Helper;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        byte[] masterKey = new byte[32];
        App.getSQRLIdentityManager().save("Kanye", masterKey, "Fy23w$sr^dJ6wdUv");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        App.getSQRLIdentityManager().removeAllIdentities();
    }

    //region TESTS

    public void testLoginButtonDisplayed() {
        assertThatButtonIsVisibleAndDisplaysCorrectText(R.id.LoginButton, R.string.login);
    }

    public void testCreateNewIdentityButtonDisplayed() {
        assertThatButtonIsVisibleAndDisplaysCorrectText(R.id.CreateNewIdentityButton, R.string.create_new_identity);
    }

    public void testLoginChoicesActivityStartedOnLoginButtonClick() throws Exception {
        assertThatButtonStartsActivity(R.id.LoginButton, LoginChoicesActivity.class);
    }

    public void testCreateNewIdentityActivityStartedOnCreateNewIdentityButtonClick() throws Exception {
        assertThatButtonStartsActivity(R.id.CreateNewIdentityButton, CreateNewIdentityActivity.class);
    }

    //endregion

    private void assertThatButtonStartsActivity(final int buttonId, Class activityToCheckFor) throws Exception {
        final ActivityInstrumentationTestCase2 instrumentationTestCase = this;
        Activity newActivity = Helper.monitorForActivity(activityToCheckFor, 5000, new Lambda() {
            public void run() {
                // Click the button
                setActivityInitialTouchMode(true);
                MainActivity activity = getActivity();
                Button button = (Button)activity.findViewById(buttonId);
                TouchUtils.clickView(instrumentationTestCase, button);
            }
        });

        // Verify that the new activity was started
        assertNotNull(newActivity);
        newActivity.finish();
    }

    private void assertThatButtonIsVisibleAndDisplaysCorrectText(int buttonId, int stringId) {
        // Get the activity
        MainActivity activity = getActivity();

        // Get the expected button text
        Resources resources = activity.getResources();
        String expected = resources.getString(stringId);

        // Assert that the button is visible and has the correct text set
        Button button = (Button)activity.findViewById(buttonId);
        assertEquals(expected, button.getText());
        assertEquals(View.VISIBLE, button.getVisibility());
    }
}
