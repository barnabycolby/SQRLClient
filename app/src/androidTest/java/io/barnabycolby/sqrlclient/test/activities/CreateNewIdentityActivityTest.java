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
import io.barnabycolby.sqrlclient.R;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
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

    @Test
    public void testBExplanationTextIsDisplayed() throws Exception {
        allowCameraPermissions();

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
        allowCameraPermissions();

        // Check button is not displayed initially
        mProgressBar.check(matches(isDisplayed()));
        mCreateButton.check(matches(not(isDisplayed())));

        // Check that button is displayed after the entropy collection has finished
        waitForEntropyCollectionToFinish();
        mProgressBar.check(matches(not(isDisplayed())));
        mCreateButton.check(matches(isDisplayed()));
    }

    @Test
    public void testBIdentityNameEditTextIsDisplayed() throws Exception {
        allowCameraPermissions();

        // Find the textbox and make sure it's visible
        mIdentityNameEditText.check(matches(isDisplayed()));
    }

    @Test
    public void testBCreateButtonEnabledOrDisabledBasedOnIdentityNameText() throws Exception {
        allowCameraPermissions();

        waitForEntropyCollectionToFinish();
        mIdentityNameEditText.check(matches(withText("")));
        mCreateButton.check(matches(not(isEnabled())));
        mIdentityNameEditText.perform(typeText("Rupert"));
        mCreateButton.check(matches(isEnabled()));
        mIdentityNameEditText.perform(clearText());
        mCreateButton.check(matches(not(isEnabled())));
    }

    private void allowCameraPermissions() throws Exception {
        UiObject allowButton = mDevice.findObject(new UiSelector()
                .text("Allow")
                .className("android.widget.Button"));
        if (allowButton.exists()) {
            allowButton.click();
        }
    }

    /**
     * TODO: Come up with a more reliable and quicker way to detect when entropy collection has finished.
     *       One possibility is to call mActivity.onEntropyCollectionFinished but this proved problematic.
     */
    private void waitForEntropyCollectionToFinish() throws Exception {
        Thread.sleep(10 * 1000);
    }
}
