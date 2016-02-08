package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.activities.CreateNewIdentityActivityTest;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private Activity mActivity;
    private Spinner mIdentitySpinner;

    @Before
    public void setUp() {
        this.mActivity = mActivityRule.getActivity();

        // Espresso doesn't seem to have an easy way to retrieve the list of spinner text
        // So we do this manually instead
        this.mIdentitySpinner = (Spinner)this.mActivity.findViewById(R.id.IdentitySpinner);
    }

    @After
    public void tearDown() throws Exception {
        // The list of identities needs to be reset, as these will persist across application instances
        App.getSQRLIdentityManager().removeAllIdentities();
    }

    @Test
    public void identityListEmptyIfNoIdentitiesExist() {
        SpinnerAdapter identitySpinnerAdapter = this.mIdentitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(0, identitySpinnerAdapter.getCount());
    }

    @Test
    public void identityListContainsCreatedIdentity() throws Exception {
        String identityName = "Alice";
        createNewIdentity(identityName);

        SpinnerAdapter identitySpinnerAdapter = this.mIdentitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(1, identitySpinnerAdapter.getCount());
        assertEquals(identityName, identitySpinnerAdapter.getItem(0));

        String identityName2 = "Barney";
        createNewIdentity(identityName2);

        identitySpinnerAdapter = this.mIdentitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(2, identitySpinnerAdapter.getCount());
        assertEquals(identityName2, identitySpinnerAdapter.getItem(1));
    }

    @Test
    public void maliciousIdentityNamesDoNotCauseProblems() throws Exception {
        String identityName = "/etc/shadow";
        createNewIdentity(identityName);

        SpinnerAdapter identitySpinnerAdapter = this.mIdentitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(identityName, identitySpinnerAdapter.getItem(0));

        String identityName2 = "beans; cat /etc/shadow";
        createNewIdentity(identityName2);

        identitySpinnerAdapter = this.mIdentitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(identityName2, identitySpinnerAdapter.getItem(1));
    }

    @Test
    public void cannotCreateTwoIdentitiesWithTheSameName() throws Exception {
        String identityName = "Oscar";
        createNewIdentity(identityName);
        createNewIdentity(identityName);

        checkToastIsDisplayed(R.string.identity_already_exists);
    }

    @Test
    public void deleteIdentityRemovesIdentityFromList() throws Exception {
        String identityName = "Zane";
        createNewIdentity(identityName);

        onView(withId(R.id.DeleteIdentityButton)).perform(click());
        checkToastIsDisplayed(R.string.identity_deleted);

        SpinnerAdapter identitySpinnerAdapter = this.mIdentitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(0, identitySpinnerAdapter.getCount());
    }

    public static void createNewIdentity(String identityName) throws Exception {
        // Create an activity monitor so that we can retrieve an instance of any activities we start
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ActivityMonitor activityMonitor = instrumentation.addMonitor(CreateNewIdentityActivity.class.getName(), null, false);

        onView(withId(R.id.CreateNewIdentityButton)).perform(click());
        UiDevice device = UiDevice.getInstance(instrumentation);
        CreateNewIdentityActivityTest.allowCameraPermissions(device);
        onView(withId(R.id.IdentityNameEditText)).perform(typeText(identityName));

        // We need to pass an instance of the initialised activity to waitForEntropyCollectionToFinish
        CreateNewIdentityActivity createNewIdentityActivity = (CreateNewIdentityActivity)instrumentation.waitForMonitorWithTimeout(activityMonitor, 5000);

        CreateNewIdentityActivityTest.waitForEntropyCollectionToFinish(createNewIdentityActivity);
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());
    }

    private void checkToastIsDisplayed(int textId) {
        onView(withText(textId)).inRoot(withDecorView(not(mActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }
}
