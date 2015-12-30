package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;

import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.R;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testLoginButtonDisplayed() {
        // Get the activity
        MainActivity activity = getActivity();

        // Get the expected button text
        Resources resources = activity.getResources();
        String expected = resources.getString(R.string.login);

        // Assert that the button is visible and has the correct text set
        Button loginButton = (Button)activity.findViewById(R.id.LoginButton);
        assertEquals(expected, loginButton.getText());
        assertEquals(View.VISIBLE, loginButton.getVisibility());
    }

    public void testLoginChoicesActivityStartedOnLoginButtonClick() {
        // Create an activity monitor to watch for the LoginChoices activity starting
        ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LoginChoicesActivity.class.getName(), null, false);

        // Click the login button
        setActivityInitialTouchMode(true);
        MainActivity activity = getActivity();
        Button loginButton = (Button)activity.findViewById(R.id.LoginButton);
        TouchUtils.clickView(this, loginButton);

        // Verify that the login choices activity was started
        Activity loginChoicesActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(loginChoicesActivity);
        loginChoicesActivity.finish();
    }
}
