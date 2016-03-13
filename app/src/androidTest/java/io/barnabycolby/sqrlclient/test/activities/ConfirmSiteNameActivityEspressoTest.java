package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.ConfirmSiteNameActivity;
import io.barnabycolby.sqrlclient.activities.EnterPasswordActivity;
import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.Helper;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ConfirmSiteNameActivityEspressoTest {
    private Uri mUri = Uri.parse("sqrl://www.grc.com/sqrl?nut=mCwPTJWrbcBNMJKc76sI8w&sfn=R1JD");

    private class ConfirmSiteNameActivityTestRule extends ActivityTestRule<ConfirmSiteNameActivity> {
        ConfirmSiteNameActivityTestRule() {
            super(ConfirmSiteNameActivity.class);
        }

        @Override
        protected void beforeActivityLaunched() {
            try {
                App.getSQRLIdentityManager().save("Barnaby Colby", new byte[32], "g3wC!rUnDN5M#TDj", null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected void afterActivityFinished() {
            try {
                App.getSQRLIdentityManager().removeAllIdentities();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected Intent getActivityIntent() {
            Intent intent = super.getActivityIntent();
            intent.setData(mUri);
            return intent;
        }
    }

    @Rule
    public ConfirmSiteNameActivityTestRule mActivityTestRule = new ConfirmSiteNameActivityTestRule();

    @Test
    public void denySiteStartsMainActivity() throws Exception {
        Activity mainActivity = Helper.monitorForActivity(MainActivity.class, 5000, new Lambda() {
            public void run() {
                onView(withId(R.id.DenySiteButton)).perform(click());
            }
        });
        assertNotNull(mainActivity);
    }

    @Test
    public void confirmSiteStartsEnterPasswordActivity() throws Exception {
        Activity enterPasswordActivity = Helper.monitorForActivity(EnterPasswordActivity.class, 5000, new Lambda() {
            public void run() {
                onView(withId(R.id.ConfirmSiteButton)).perform(click());
            }
        });
        assertNotNull(enterPasswordActivity);

        // Check that the SQRL Uri was passed along
        assertEquals(this.mUri, enterPasswordActivity.getIntent().getData());
    }
}
