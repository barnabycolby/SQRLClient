package io.barnabycolby.sqrlclient.test.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.activities.NoIdentityActivity;
import io.barnabycolby.sqrlclient.App;

import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {
    /**
     * ActivityTestRule that overrides before and after activity launch methods, in order to monitor whether the NoIdentityActivity is launched as soon as the activity is started.
     *
     * This could not be done using standard JUnit before and after methods as the before method only runs once the activity has been launched.
     */
    private class MainActivityTestRule extends ActivityTestRule<MainActivity> {
        protected Instrumentation mInstrumentation;
        protected ActivityMonitor mNoIdentityActivityMonitor;

        public MainActivityTestRule() {
            super(MainActivity.class);
        }

        @Override
        protected void beforeActivityLaunched() {
            this.mInstrumentation = InstrumentationRegistry.getInstrumentation();
            this.mNoIdentityActivityMonitor = this.mInstrumentation.addMonitor(NoIdentityActivity.class.getName(), null, false);
        }

        @Override
        protected void afterActivityFinished() {
            this.mInstrumentation.removeMonitor(mNoIdentityActivityMonitor);
        }

        public ActivityMonitor getActivityMonitor() {
            return this.mNoIdentityActivityMonitor;
        }
    };

    @Rule
    public MainActivityTestRule mActivityTestRule = new MainActivityTestRule();

    @After
    public void tearDown() throws Exception {
        App.getSQRLIdentityManager().removeAllIdentities();
    }

    @Test
    public void redirectsToNoIdentityActivityIfNoIdentitiesExist() {
        // As we haven't actually created any identities, all we need to do is check that the NoIdentityActivity has been launched
        assertEquals(1, this.mActivityTestRule.getActivityMonitor().getHits());
    }
}
