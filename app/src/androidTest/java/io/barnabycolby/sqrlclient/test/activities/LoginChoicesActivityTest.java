package io.barnabycolby.sqrlclient.test.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.activities.LoginChoicesActivity;
import io.barnabycolby.sqrlclient.R;

public class LoginChoicesActivityTest extends ActivityInstrumentationTestCase2<LoginChoicesActivity> {
    public LoginChoicesActivityTest() {
        super(LoginChoicesActivity.class);
    }

    public void testUsageMessageDisplayed() {
        // Get the activity
        LoginChoicesActivity activity = getActivity();

        // Get the expected message
        Resources resources = activity.getResources();
        String expected = resources.getString(R.string.login_choices_message);

        // Assert that the correct message is displayed and is visible
        TextView messageBox = (TextView)activity.findViewById(R.id.LoginChoicesTextView);
        assertEquals(expected, messageBox.getText());
        assertEquals(View.VISIBLE, messageBox.getVisibility());
    }
}
