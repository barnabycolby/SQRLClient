package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.EnterNewPasswordActivity;
import io.barnabycolby.sqrlclient.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EnterNewPasswordActivityTest {
    @Rule
    public ActivityTestRule<EnterNewPasswordActivity> mActivityTestRule = new ActivityTestRule<EnterNewPasswordActivity>(EnterNewPasswordActivity.class);

    private Activity mActivity;

    private ViewInteraction mExplanationTextView;
    private ViewInteraction mPasswordEditText;
    private ViewInteraction mSecondPasswordEditText;
    private ViewInteraction mPasswordStrengthMeter;
    private ViewInteraction mNextButton;

    @Before
    public void setUp() {
        this.mActivity = mActivityTestRule.getActivity();

        this.mExplanationTextView = onView(withId(R.id.ExplanationTextView));
        this.mPasswordEditText = onView(withId(R.id.PasswordEditText));
        this.mSecondPasswordEditText = onView(withId(R.id.SecondPasswordEditText));
        this.mPasswordStrengthMeter = onView(withId(R.id.PasswordStrengthMeter));
        this.mNextButton = onView(withId(R.id.NextButton));
    }

    @After
    public void tearDown() {
        this.mActivity.finish();
    }

    @Test
    public void initialUIStateIsCorrect() {
        this.mExplanationTextView.check(matches(isDisplayed()));
        this.mExplanationTextView.check(matches(withText(R.string.enter_new_password_explanation)));
        this.mPasswordEditText.check(matches(isDisplayed()));
        this.mPasswordEditText.check(matches(withHint(R.string.password_hint)));
        this.mSecondPasswordEditText.check(matches(isDisplayed()));
        this.mSecondPasswordEditText.check(matches(withHint(R.string.second_password_hint)));
        this.mPasswordStrengthMeter.check(matches(isDisplayed()));
        this.mNextButton.check(matches(isDisplayed()));
        this.mNextButton.check(matches(not(isEnabled())));
    }

    @Test
    public void nextButtonStaysDisabledIfPasswordsDoNotMatch() {
        this.mPasswordEditText.perform(typeText("monkey123"));
        this.mSecondPasswordEditText.perform(typeText("brains56"));
        this.mNextButton.check(matches(not(isEnabled())));
    }

    @Test
    public void nextButtonEnabledIfPasswordsMatch() {
        String password = "changeme";
        this.mPasswordEditText.perform(typeText(password));
        this.mSecondPasswordEditText.perform(typeText(password));
        this.mNextButton.check(matches(isEnabled()));
    }

    @Test
    public void nextButtonDisabledIfTextChangedAfterPasswordsMatch() {
        String password = "P4$$w0rd";
        this.mPasswordEditText.perform(typeText(password));
        this.mSecondPasswordEditText.perform(typeText(password));
        this.mPasswordEditText.perform(typeText("beans"));
        this.mNextButton.check(matches(not(isEnabled())));
    }
}
