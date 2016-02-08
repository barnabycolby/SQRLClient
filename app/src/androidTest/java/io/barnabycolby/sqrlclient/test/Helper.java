package io.barnabycolby.sqrlclient.test;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.support.test.espresso.Espresso;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.R;
import io.barnabycolby.sqrlclient.test.activities.CreateNewIdentityActivityTest;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class Helper {

    public static void createNewIdentity(String identityName) throws Exception {
        // Create an activity monitor so that we can retrieve an instance of any activities we start
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ActivityMonitor activityMonitor = instrumentation.addMonitor(CreateNewIdentityActivity.class.getName(), null, false);

        onView(withId(R.id.CreateNewIdentityButton)).perform(click());
        UiDevice device = UiDevice.getInstance(instrumentation);
        CreateNewIdentityActivityTest.allowCameraPermissions(device);
        onView(withId(R.id.IdentityNameEditText)).perform(typeText(identityName));
        Espresso.closeSoftKeyboard();

        // We need to pass an instance of the initialised activity to waitForEntropyCollectionToFinish
        CreateNewIdentityActivity createNewIdentityActivity = (CreateNewIdentityActivity)instrumentation.waitForMonitorWithTimeout(activityMonitor, 5000);
        instrumentation.removeMonitor(activityMonitor);

        CreateNewIdentityActivityTest.waitForEntropyCollectionToFinish(createNewIdentityActivity);
        onView(withId(R.id.CreateNewIdentityButton)).perform(click());
    }
}
