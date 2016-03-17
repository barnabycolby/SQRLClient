package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.R;

import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {
    private class MainActivityTestRule extends ActivityTestRule<MainActivity> {
        public MainActivityTestRule() {
            super(MainActivity.class);
        }

        @Override
        protected void beforeActivityLaunched() {
            try {
                App.getSQRLIdentityManager().save("Eleri", new byte[32], "&Nx^!sLQ47KTqubw", null);
            } catch (IdentityAlreadyExistsException ex) {
                // Do nothing
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    @Rule
    public MainActivityTestRule mActivityTestRule = new MainActivityTestRule();

    @After
    public void tearDown() throws Exception {
        App.getSQRLIdentityManager().removeAllIdentities();
    }

    @Test
    public void loginOnThisDeviceTipDisplayed() {
        ViewInteraction textView = onView(withId(R.id.LoginOnDeviceTextView));
        textView.check(matches(isDisplayed()));
        textView.check(matches(withText(R.string.login_on_this_device)));
    }
}
