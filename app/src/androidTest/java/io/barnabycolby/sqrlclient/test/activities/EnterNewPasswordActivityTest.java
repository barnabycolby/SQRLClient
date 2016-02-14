package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.EnterNewPasswordActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.Helper;
import io.barnabycolby.sqrlclient.test.Helper.Lambda;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class EnterNewPasswordActivityTest {
    @Rule
    public ActivityTestRule<EnterNewPasswordActivity> mActivityTestRule = new ActivityTestRule<EnterNewPasswordActivity>(EnterNewPasswordActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context context = InstrumentationRegistry.getInstrumentation().getContext();
            Intent intent = new Intent(context, EnterNewPasswordActivity.class);
            intent.putExtra("identityName", "Roald Dahl");
            intent.putExtra("masterKey", new byte[32]);
            return intent;
        }
    };

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
        this.mPasswordEditText.perform(typeText("&HCbmL5KZSjN9o5e"));
        this.mSecondPasswordEditText.perform(typeText("OZAU22aFFn^$Oqw$"));
        this.mNextButton.check(matches(not(isEnabled())));
    }

    @Test
    public void nextButtonEnabledIfPasswordsMatchAndPasswordIsStrong() {
        String password = "%%QKyF0cO*%kP&*@";
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

    @Test
    public void nextButtonRedirectsToMainActivity() throws Exception {
        String password = "08v$Z!U#qm$EGB^X";
        this.mPasswordEditText.perform(typeText(password));
        this.mSecondPasswordEditText.perform(typeText(password));
        Espresso.closeSoftKeyboard();

        Activity mainActivity = Helper.monitorForActivity(MainActivity.class, 5000, new Lambda() {
            public void run() {
                mNextButton.perform(click());
            }
        });
        assertNotNull(mainActivity);
    }

    @Test
    public void throwsExceptionIfMissingRequiredIntentExtras() throws Exception {
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        final Intent intent = new Intent(instrumentation.getContext(), EnterNewPasswordActivity.class);
        Helper.assertExceptionThrown(RuntimeException.class, new Lambda() {
            public void run() throws Exception {
                instrumentation.startActivitySync(intent);
            }
        });
    }

    @Test
    public void nextButtonDisabledIfWeakPasswordIsUsed() {
        String weakPassword = "Monkey123";
        this.mPasswordEditText.perform(typeText(weakPassword));
        this.mSecondPasswordEditText.perform(typeText(weakPassword));
        this.mNextButton.check(matches(not(isEnabled())));
    }
}
