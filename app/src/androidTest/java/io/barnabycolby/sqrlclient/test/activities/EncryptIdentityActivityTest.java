package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.EncryptIdentityActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.helpers.Helper;
import io.barnabycolby.sqrlclient.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EncryptIdentityActivityTest {

    private EncryptIdentityActivity mActivity;

    // Espresso UI components
    private ViewInteraction mEncryptProgressBar;
    private ViewInteraction mExplanationTextView;

    private class TestRule extends ActivityTestRule<EncryptIdentityActivity> {
        public TestRule() {
            super(EncryptIdentityActivity.class);
        }

        @Override
        protected Intent getActivityIntent() {
            Intent intent = super.getActivityIntent();
            Bundle bundle = new Bundle();
            bundle.putString("identityName", "Noam Chomsky");
            bundle.putByteArray("masterKey", Helper.hexStringToByteArray("250528BEF79D42413248BAB67CC63A4B381F5004B6644F5D6EF6C1ECD8542087"));
            bundle.putString("password", "S9dE%4kAYVRRkcPI");

            // We want to disable the async task so that we can perform Espresso tests
            bundle.putBoolean("disableAsyncTasks", true);

            intent.putExtras(bundle);

            return intent;
        }
    }

    @Rule
    public TestRule mActivityTestRule = new TestRule();

    @Before
    public void setUp() throws Exception {
        this.mActivity = this.mActivityTestRule.getActivity();

        // Get espresso references to the UI components
        this.mExplanationTextView = onView(withId(R.id.ExplanationTextView));
        this.mEncryptProgressBar = onView(withId(R.id.EncryptProgressBar));
    }

    @After
    public void tearDown() throws Exception {
        App.getSQRLIdentityManager().removeAllIdentities();
    }

    @Test
    public void displaysCorrectInitialUIComponents() {
        this.mExplanationTextView.check(matches(isDisplayed()));
        this.mExplanationTextView.check(matches(withText(R.string.encrypting_identity_explanation)));
        this.mEncryptProgressBar.check(matches(isDisplayed()));
    }
}
