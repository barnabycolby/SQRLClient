package io.barnabycolby.sqrlclient.test.activities;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.EnterPasswordActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EnterPasswordActivityTest {

    private ViewInteraction mIdentitySpinner;
    private ViewInteraction mPasswordEditText;
    private ViewInteraction mVerifyProgressBar;
    private ViewInteraction mInformationTextView;
    private ViewInteraction mLoginButton;

    private class TestRule extends ActivityTestRule<EnterPasswordActivity> {
        public TestRule() {
            super(EnterPasswordActivity.class);
        }

        @Override
        public void beforeActivityLaunched() {
            // Set up the identities
            try {
                App.getSQRLIdentityManager().save("Harriet", new byte[32]);
                App.getSQRLIdentityManager().save("Rupert", new byte[32]);
            } catch (Exception ex) {
                // Do nothing if an exception gets thrown
            }
        }

        @Override
        public void afterActivityFinished() {
            try {
                App.getSQRLIdentityManager().removeAllIdentities();
            } catch (Exception ex) {
                // Do nothing if an exception gets thrown
            }
        }
    }

    @Rule
    public TestRule mActivityTestRule = new TestRule();

    @Before
    public void setUp() throws Exception {
        // Get espresso references to the UI components
        this.mIdentitySpinner = onView(withId(R.id.IdentitySpinner));
        this.mPasswordEditText = onView(withId(R.id.PasswordEditText));
        this.mVerifyProgressBar = onView(withId(R.id.VerifyProgressBar));
        this.mInformationTextView = onView(withId(R.id.InformationTextView));
        this.mLoginButton = onView(withId(R.id.LoginButton));
    }

    @Test
    public void displaysCorrectInitialUIComponents() {
        this.mIdentitySpinner.check(matches(isDisplayed()));
        this.mPasswordEditText.check(matches(isDisplayed()));
        this.mVerifyProgressBar.check(matches(not(isDisplayed())));
        this.mInformationTextView.check(matches(isDisplayed()));
        this.mInformationTextView.check(matches(withText(R.string.enter_password_help)));
        this.mLoginButton.check(matches(isDisplayed()));
        this.mLoginButton.check(matches(not(isEnabled())));
    }

    @Test
    public void loginButtonEnabledIfPasswordNotBlank() {
        this.mPasswordEditText.perform(typeText("brains"));
        this.mLoginButton.check(matches(isEnabled()));
        this.mPasswordEditText.perform(clearText());
        this.mLoginButton.check(matches(not(isEnabled())));
        this.mPasswordEditText.perform(typeText("zombies"));
        this.mLoginButton.check(matches(isEnabled()));
    }
}
