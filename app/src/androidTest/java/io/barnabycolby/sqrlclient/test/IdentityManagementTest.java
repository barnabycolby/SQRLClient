package io.barnabycolby.sqrlclient.test;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

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
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class IdentityManagementTest {
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

    private void createNewIdentity(String identityName) throws Exception {
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        CreateNewIdentityActivityTest.allowCameraPermissions(device);
        onView(withId(R.id.IdentityNameEditText)).perform(typeText(identityName));
        CreateNewIdentityActivityTest.waitForEntropyCollectionToFinish();
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());
    }
}
