package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.activities.EnterNewPasswordActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.TestHelper;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateNewIdentityActivityTest {
    private CreateNewIdentityActivity mActivity;
    private UiDevice mDevice;
    private ViewInteraction mProgressBar;
    private ViewInteraction mCreateButton;
    private ViewInteraction mIdentityNameEditText;

    @Rule
    public ActivityTestRule<CreateNewIdentityActivity> mActivityTestRule = new ActivityTestRule<CreateNewIdentityActivity>(CreateNewIdentityActivity.class);

    @Before
    public void setUp() throws Exception {
        this.mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        this.mActivity = mActivityTestRule.getActivity();

        this.mProgressBar = onView(withId(R.id.EntropyHarvesterProgressBar));
        this.mCreateButton = onView(withId(R.id.CreateNewIdentityButton));
        this.mIdentityNameEditText = onView(withId(R.id.IdentityNameEditText));
    }

    // Disabled this test temporarily whilst a solution is found to interference caused by other tests
    // We can guarantee the order of tests that run in this class but not in others
    // If a test in another class runs before this test, and it allows permissions, then this test will fail
    // Question asked on StackOverflow: http://stackoverflow.com/questions/35189425/android-testing-problems-caused-by-state-persisting-across-application-instanc
    /*
    @Test
    public void testAErrorMessageDisplayedWhenPermissionsDenied() throws Exception {
        // Click the deny button
        UiObject denyButton = mDevice.findObject(new UiSelector()
                .text("Deny")
                .className("android.widget.Button"));
        denyButton.click();

        // Verify that the text is displayed
        Resources resources = mActivity.getResources();
        String expected = resources.getString(R.string.camera_permission_not_granted);
        onView(withId(R.id.ErrorTextView)).check(matches(withText(expected)));
    }
    */

    @Test
    public void testBExplanationTextIsDisplayed() throws Exception {
        allowCameraPermissions(this.mDevice);

        // Get the expected explanation text
        Resources resources = mActivity.getResources();
        String expected = resources.getString(R.string.create_new_identity_explanation);

        // Assert that the appropriate TextView is visible and shows the correct text
        TextView explanationTextView = (TextView)mActivity.findViewById(R.id.CreateNewIdentityExplanationTextView);
        assertEquals(expected, explanationTextView.getText());
        assertEquals(View.VISIBLE, explanationTextView.getVisibility());
    }

    @Test
    public void testBProgressBarReplacedByCreateButtonAfterEntropyCollectionFinished() throws Exception {
        allowCameraPermissions(this.mDevice);

        // Check button is not displayed initially
        mProgressBar.check(matches(isDisplayed()));
        mCreateButton.check(matches(not(isDisplayed())));

        // Check that button is displayed after the entropy collection has finished
        waitForEntropyCollectionToFinish(mActivity);
        mProgressBar.check(matches(not(isDisplayed())));
        mCreateButton.check(matches(isDisplayed()));
    }

    @Test
    public void testBIdentityNameEditTextIsDisplayed() throws Exception {
        allowCameraPermissions(this.mDevice);

        mIdentityNameEditText.check(matches(isDisplayed()));
        mIdentityNameEditText.check(matches(withHint(R.string.new_identity_name_hint)));
    }

    @Test
    public void testBCreateButtonEnabledOrDisabledBasedOnIdentityNameText() throws Exception {
        allowCameraPermissions(this.mDevice);

        waitForEntropyCollectionToFinish(mActivity);
        mIdentityNameEditText.check(matches(withText("")));
        mCreateButton.check(matches(not(isEnabled())));
        mIdentityNameEditText.perform(typeText("Rupert"));
        mCreateButton.check(matches(isEnabled()));
        mIdentityNameEditText.perform(clearText());
        mCreateButton.check(matches(not(isEnabled())));
    }

    @Test
    public void testBEnterNewPasswordActivityStartedOnCreateClick() throws Exception {
        allowCameraPermissions(this.mDevice);
        waitForEntropyCollectionToFinish(mActivity);
        mIdentityNameEditText.perform(typeText("Jason Statham"));

        Activity enterNewPasswordActivity = TestHelper.monitorForActivity(EnterNewPasswordActivity.class, 5000, new Lambda() {
            public void run() {
                mCreateButton.perform(click());
            }
        });

        // Cleanup
        App.getSQRLIdentityManager().removeAllIdentities();

        assertNotNull(enterNewPasswordActivity);
    }

    public static void allowCameraPermissions(UiDevice device) throws Exception {
        UiObject allowButton = device.findObject(new UiSelector()
                .text("Allow")
                .className("android.widget.Button"));
        if (allowButton.exists()) {
            allowButton.click();
        }
    }

    public static void waitForEntropyCollectionToFinish(CreateNewIdentityActivity createNewIdentityActivity) throws Exception {
        createNewIdentityActivity.onEntropyCollectionFinished();
    }
}
