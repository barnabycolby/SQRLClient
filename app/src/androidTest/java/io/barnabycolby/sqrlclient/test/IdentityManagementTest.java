package io.barnabycolby.sqrlclient.test;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.support.test.espresso.Espresso;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.activities.NoIdentityActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentityManager;
import io.barnabycolby.sqrlclient.test.activities.CreateNewIdentityActivityTest;
import io.barnabycolby.sqrlclient.test.TestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static io.barnabycolby.sqrlclient.test.TestHelper.withSpinnerItemText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
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
    private String mPassword = "ZS9rtA#pTAtm48!q";

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
        TestHelper.createNewIdentity(identityName);

        onView(withId(R.id.IdentitySpinner)).check(matches(withSpinnerItemText(identityName)));

        String identityName2 = "Barney";
        TestHelper.createNewIdentity(identityName2);

        onView(withId(R.id.IdentitySpinner)).check(matches(withSpinnerItemText(identityName2)));
    }

    @Test
    public void maliciousIdentityNamesDoNotCauseProblems() throws Exception {
        String identityName = "/etc/shadow";
        TestHelper.createNewIdentity(identityName);

        onView(withId(R.id.IdentitySpinner)).check(matches(withSpinnerItemText(identityName)));

        String identityName2 = "beans; cat /etc/shadow";
        TestHelper.createNewIdentity(identityName2);

        onView(withId(R.id.IdentitySpinner)).check(matches(withSpinnerItemText(identityName2)));
    }

    @Test
    public void cannotCreateTwoIdentitiesWithTheSameName() throws Exception {
        final String identityName = "Oscar";
        TestHelper.createNewIdentity(identityName);

        // Try to recreate an identity with the same name
        CreateNewIdentityActivity createnewIdentityActivity = (CreateNewIdentityActivity)TestHelper.monitorForActivity(CreateNewIdentityActivity.class, 5000, new Lambda() {
            public void run() throws Exception {
                onView(withId(R.id.CreateNewIdentityButton)).perform(click());
                UiDevice device = UiDevice.getInstance(mInstrumentation);
                CreateNewIdentityActivityTest.allowCameraPermissions(device);
                onView(withId(R.id.IdentityNameEditText)).perform(typeText(identityName));
                Espresso.closeSoftKeyboard();
            }
        });
        CreateNewIdentityActivityTest.waitForEntropyCollectionToFinish(createnewIdentityActivity);
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());

        checkToastIsDisplayed(R.string.identity_already_exists);
    }

    @Test
    public void deleteIdentityRemovesIdentityFromList() throws Exception {
        // We need to create two identities, as otherwise when we delete the identity,
        // the NoIdentityActivity will be started and the list won't be updated
        TestHelper.createNewIdentity("Zane Lowe");
        TestHelper.createNewIdentity("David Rodigan");

        onView(withId(R.id.DeleteIdentityButton)).perform(click());
        checkToastIsDisplayed(R.string.identity_deleted);

        Spinner identitySpinner = this.getIdentitySpinner();
        SpinnerAdapter identitySpinnerAdapter = identitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(1, identitySpinnerAdapter.getCount());
    }

    @Test
    public void redirectToNoIdentityPageOnceAllIdentitiesAreDeleted() throws Exception {
        String identityName = "Bruce Schneier";
        TestHelper.createNewIdentity(identityName);

        NoIdentityActivity activity = (NoIdentityActivity)TestHelper.monitorForActivity(NoIdentityActivity.class, 5000, new Lambda() {
            public void run() throws Exception {
                onView(withId(R.id.DeleteIdentityButton)).perform(click());
            }
        });
        assertNotNull(activity);
    }

    @Test
    public void currentIdentityIsFirstIdentityWhenCreated() throws Exception {
        String identityName = "Martin Fowler";
        TestHelper.createNewIdentity(identityName);
        assertEquals(identityName, App.getSQRLIdentityManager().getCurrentIdentityName());
    }

    @Test
    public void selectedIdentityMirrorsCurrentIdentityOnMainActivityLoad() throws Exception {
        // Initially we start on the no identity activity
        // To avoid automatically being switched to the main activity, we perform the identity set up manually
        SQRLIdentityManager identityManager = App.getSQRLIdentityManager();
        String identityName1 = "Pablo Escobar";
        String identityName2 = "Walter White";
        identityManager.save(identityName1, new byte[32], mPassword, null);
        identityManager.save(identityName2, new byte[32], mPassword, null);
        identityManager.setCurrentIdentity(identityName2);

        // Trigger the transition back to MainActivity by going to the create identity activity and pressing back
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.IdentitySpinner)).check(matches(withSpinnerText(identityName2)));
    }

    @Test
    public void currentIdentityUpdatedAfterCurrentIdentityDeleted() throws Exception {
        String identityName1 = "Alan Turing";
        String identityName2 = "Charles Babbage";
        TestHelper.createNewIdentity(identityName1);
        TestHelper.createNewIdentity(identityName2);

        assertEquals(identityName1, App.getSQRLIdentityManager().getCurrentIdentityName());
        onView(withId(R.id.DeleteIdentityButton)).perform(click());
        assertEquals(identityName2, App.getSQRLIdentityManager().getCurrentIdentityName());
    }

    @Test
    public void currentIdentityUpdatedWhenSpinnerSelected() throws Exception {
        String identityName1 = "Dan North";
        String identityName2 = "Haskell Curry";
        TestHelper.createNewIdentity(identityName1);
        TestHelper.createNewIdentity(identityName2);

        assertEquals(identityName1, App.getSQRLIdentityManager().getCurrentIdentityName());
        selectIdentitySpinnerItem(identityName2);
        assertEquals(identityName2, App.getSQRLIdentityManager().getCurrentIdentityName());
        selectIdentitySpinnerItem(identityName1);
        assertEquals(identityName1, App.getSQRLIdentityManager().getCurrentIdentityName());
    }

    @Test
    public void identityNotCreatedIfCancelledOnEnterNewPasswordActivity() throws Exception {
        TestHelper.createNewIdentity("Oliver");
        CreateNewIdentityActivity createNewIdentityActivity = (CreateNewIdentityActivity)TestHelper.monitorForActivity(CreateNewIdentityActivity.class, 5000, new Lambda() {
            public void run() throws Exception {
                onView(withId(R.id.CreateNewIdentityButton)).perform(click());
                UiDevice device = UiDevice.getInstance(mInstrumentation);
                CreateNewIdentityActivityTest.allowCameraPermissions(device);
                onView(withId(R.id.IdentityNameEditText)).perform(typeText("Tom"));
                Espresso.closeSoftKeyboard();
            }
        });

        CreateNewIdentityActivityTest.waitForEntropyCollectionToFinish(createNewIdentityActivity);
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());

        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.IdentitySpinner)).check(matches(not(withSpinnerItemText("Tom"))));

        // Cleanup
        App.getSQRLIdentityManager().removeAllIdentities();        
    }

    private void selectIdentitySpinnerItem(String item) {
        onView(withId(R.id.IdentitySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(item))).perform(click());
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
