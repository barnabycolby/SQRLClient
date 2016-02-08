package io.barnabycolby.sqrlclient.test;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.activities.NoIdentityActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.Helper;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
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
public class IdentityManagementTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private Instrumentation mInstrumentation;
    private ActivityMonitor mMainActivityMonitor;
    private MainActivity mMainActivity;

    @Before
    public void setUp() {
        this.mInstrumentation = InstrumentationRegistry.getInstrumentation();
        this.mMainActivityMonitor = this.mInstrumentation.addMonitor(MainActivity.class.getName(), null, false);
    }

    @After
    public void tearDown() throws Exception {
        // The list of identities needs to be reset, as these will persist across application instances
        App.getSQRLIdentityManager().removeAllIdentities();

        this.mInstrumentation.removeMonitor(this.mMainActivityMonitor);
    }

    @Test
    public void identityListContainsCreatedIdentity() throws Exception {
        String identityName = "Alice";
        Helper.createNewIdentity(identityName);

        Spinner identitySpinner = this.getIdentitySpinner();
        SpinnerAdapter identitySpinnerAdapter = identitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(1, identitySpinnerAdapter.getCount());
        assertEquals(identityName, identitySpinnerAdapter.getItem(0));

        String identityName2 = "Barney";
        Helper.createNewIdentity(identityName2);

        identitySpinnerAdapter = identitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(2, identitySpinnerAdapter.getCount());
        assertEquals(identityName2, identitySpinnerAdapter.getItem(1));
    }

    @Test
    public void maliciousIdentityNamesDoNotCauseProblems() throws Exception {
        String identityName = "/etc/shadow";
        Helper.createNewIdentity(identityName);

        Spinner identitySpinner = this.getIdentitySpinner();
        SpinnerAdapter identitySpinnerAdapter = identitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(identityName, identitySpinnerAdapter.getItem(0));

        String identityName2 = "beans; cat /etc/shadow";
        Helper.createNewIdentity(identityName2);

        identitySpinnerAdapter = identitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(identityName2, identitySpinnerAdapter.getItem(1));
    }

    @Test
    public void cannotCreateTwoIdentitiesWithTheSameName() throws Exception {
        String identityName = "Oscar";
        Helper.createNewIdentity(identityName);
        Helper.createNewIdentity(identityName);

        checkToastIsDisplayed(R.string.identity_already_exists);
    }

    @Test
    public void deleteIdentityRemovesIdentityFromList() throws Exception {
        String identityName = "Zane";
        Helper.createNewIdentity(identityName);

        onView(withId(R.id.DeleteIdentityButton)).perform(click());
        checkToastIsDisplayed(R.string.identity_deleted);

        Spinner identitySpinner = this.getIdentitySpinner();
        SpinnerAdapter identitySpinnerAdapter = identitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(0, identitySpinnerAdapter.getCount());
    }

    private void checkToastIsDisplayed(int textId) {
        onView(withText(textId)).inRoot(withDecorView(not(getMainActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    private Spinner getIdentitySpinner() {
        return (Spinner)getMainActivity().findViewById(R.id.IdentitySpinner);
    }

    private MainActivity getMainActivity() {
        if (this.mMainActivity == null) {
            this.mMainActivity = (MainActivity)this.mInstrumentation.waitForMonitorWithTimeout(this.mMainActivityMonitor, 5000);
            assertNotNull(this.mMainActivity);
        }

        return this.mMainActivity;
    }
}
