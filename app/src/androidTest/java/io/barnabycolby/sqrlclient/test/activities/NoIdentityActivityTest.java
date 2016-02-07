package io.barnabycolby.sqrlclient.test.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.NoIdentityActivity;
import io.barnabycolby.sqrlclient.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NoIdentityActivityTest {
    private NoIdentityActivity mActivity;

    @Rule
    public ActivityTestRule<NoIdentityActivity> mActivityTestRule = new ActivityTestRule<NoIdentityActivity>(NoIdentityActivity.class);

    @Before
    public void setUp() throws Exception {
        this.mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void createIdentityTextIsDisplayed() {
        onView(withId(R.id.NoIdentityExplanation)).check(matches(isDisplayed()));
        onView(withId(R.id.NoIdentityExplanation)).check(matches(withText(R.string.no_identity_explanation)));
    }
}
