package io.barnabycolby.sqrlclient.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.support.test.espresso.Espresso;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.helpers.Lambda;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.activities.CreateNewIdentityActivityTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.fail;

public class TestHelper {

    public static void createNewIdentity(final String identityName) throws Exception {
        // Create an activity monitor so that we can retrieve an instance of any activities we start
        CreateNewIdentityActivity createNewIdentityActivity = (CreateNewIdentityActivity)monitorForActivity(CreateNewIdentityActivity.class, 5000, new Lambda() {
            public void run() throws Exception {
                onView(withId(R.id.CreateNewIdentityButton)).perform(click());
                UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
                CreateNewIdentityActivityTest.allowCameraPermissions(device);
                onView(withId(R.id.IdentityNameEditText)).perform(typeText(identityName));
                Espresso.closeSoftKeyboard();
            }
        });

        // We need to pass an instance of the initialised activity to waitForEntropyCollectionToFinish
        CreateNewIdentityActivityTest.waitForEntropyCollectionToFinish(createNewIdentityActivity);
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());

        // Fill in the enter new password activity
        String password = "h$UpKd7x8AIxPCQ^";
        onView(withId(R.id.PasswordEditText)).perform(typeText(password));
        onView(withId(R.id.SecondPasswordEditText)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.NextButton)).perform(click());
    }

    /**
     * Monitors a lambda to catch a specific activity if it is launched.
     *
     * @param activityToCheckFor  The class of the activity to monitor for.
     * @param timeOut  The length of time to wait for the activity for once the lambda has finished execution.
     * @param lambda  The code to monitor.
     * @throws Exception  If the lambda throws an exception.
     */
    public static Activity monitorForActivity(Class activityToCheckFor, int timeOut, Lambda lambda) throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ActivityMonitor activityMonitor = instrumentation.addMonitor(activityToCheckFor.getName(), null, false);

        lambda.run();

        Activity activity = instrumentation.waitForMonitorWithTimeout(activityMonitor, timeOut);
        instrumentation.removeMonitor(activityMonitor);
        return activity;
    }

    public static <E extends Exception> void assertExceptionThrown(Class<E> exceptionClass, Lambda lambda) throws Exception {
        try {
            lambda.run();
        } catch (Exception ex) {
            if (exceptionClass.isInstance(ex)) {
                return;
            } else {
                throw ex;
            }
        }

        fail(exceptionClass.getSimpleName() + " was not thrown.");
    }

    public static Matcher<View> withSpinnerItemText(final String expectedText) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof Spinner)) {
                    return false;
                }

                Spinner spinner = (Spinner)view;
                SpinnerAdapter adapter = spinner.getAdapter();
                if (adapter == null) {
                    return false;
                }

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (!(adapter.getItem(i) instanceof String)) {
                        continue;
                    }

                    String itemText = (String)adapter.getItem(i);
                    if (expectedText.equals(itemText)) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public void describeTo(Description description) {}
        };
    }
}
