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
import io.barnabycolby.sqrlclient.R;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    //region TESTS

    public void testLoginButtonDisplayed() {
        assertThatButtonIsVisibleAndDisplaysCorrectText(R.id.LoginButton, R.string.login);
    }

    public void testCreateNewIdentityButtonDisplayed() {
        assertThatButtonIsVisibleAndDisplaysCorrectText(R.id.CreateNewIdentityButton, R.string.create_new_identity);
    }

    public void testLoginChoicesActivityStartedOnLoginButtonClick() {
        assertThatButtonStartsActivity(R.id.LoginButton, LoginChoicesActivity.class);
    }

    public void testCreateNewIdentityActivityStartedOnCreateNewIdentityButtonClick() {
        assertThatButtonStartsActivity(R.id.CreateNewIdentityButton, CreateNewIdentityActivity.class);
    }

    //endregion

    private void assertThatButtonStartsActivity(int buttonId, Class activityToCheckFor) {
        // Create an activity monitor to watch for the activity starting
        ActivityMonitor activityMonitor = getInstrumentation().addMonitor(activityToCheckFor.getName(), null, false);

        // Click the button
        setActivityInitialTouchMode(true);
        MainActivity activity = getActivity();
        Button button = (Button)activity.findViewById(buttonId);
        TouchUtils.clickView(this, button);

        // Verify that the new activity was started
        Activity newActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
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