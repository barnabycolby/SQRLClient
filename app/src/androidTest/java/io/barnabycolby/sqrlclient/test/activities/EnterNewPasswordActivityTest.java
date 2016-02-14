package io.barnabycolby.sqrlclient.test.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.EnterNewPasswordActivity;
import io.barnabycolby.sqrlclient.R;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EnterNewPasswordActivityTest {
    @Rule
    public ActivityTestRule<EnterNewPasswordActivity> mActivityTestRule = new ActivityTestRule<EnterNewPasswordActivity>(EnterNewPasswordActivity.class);

    @Test
    public void nextButtonDisabledInitially() {
        onView(withId(R.id.NextButton)).check(matches(not(isEnabled())));
    }
}
