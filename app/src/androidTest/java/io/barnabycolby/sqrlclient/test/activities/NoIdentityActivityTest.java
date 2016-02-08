package io.barnabycolby.sqrlclient.test.activities;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.support.test.espresso.ViewInteraction;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.activities.NoIdentityActivity;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.IdentityManagementTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NoIdentityActivityTest {
    private NoIdentityActivity mActivity;
    private ViewInteraction mCreateNewIdentityButton;

    @Rule
    public ActivityTestRule<NoIdentityActivity> mActivityTestRule = new ActivityTestRule<NoIdentityActivity>(NoIdentityActivity.class);

    @Before
    public void setUp() throws Exception {
        this.mActivity = mActivityTestRule.getActivity();
        this.mCreateNewIdentityButton = onView(withId(R.id.CreateNewIdentityButton));
    }

    @Test
    public void noIdentityExplanationTextIsDisplayed() {
        onView(withId(R.id.NoIdentityExplanation)).check(matches(isDisplayed()));
        onView(withId(R.id.NoIdentityExplanation)).check(matches(withText(R.string.no_identity_explanation)));
    }

    @Test
    public void createNewIdentityButtonTakesYouToTheCreateNewIdentityActivity() {
        // Set up the activity monitor
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ActivityMonitor activityMonitor = instrumentation.addMonitor(CreateNewIdentityActivity.class.getName(), null, false);

        // Click the button
        mCreateNewIdentityButton.check(matches(isDisplayed()));
        mCreateNewIdentityButton.perform(click());

        // Check the activity was started
        CreateNewIdentityActivity createNewIdentityActivity = (CreateNewIdentityActivity)instrumentation.waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(createNewIdentityActivity);
    }

    @Test
    public void createIdentityNavigatesToMainActivity() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ActivityMonitor activityMonitor = instrumentation.addMonitor(MainActivity.class.getName(), null, false);

        IdentityManagementTest.createNewIdentity("Elon Musk");

        MainActivity mainActivity = (MainActivity)instrumentation.waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(mainActivity);
    }
}
