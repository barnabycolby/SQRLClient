package io.barnabycolby.sqrlclient.test;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import io.barnabycolby.sqrlclient.activities.MainActivity;
import io.barnabycolby.sqrlclient.R;

import org.junit.runner.RunWith;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class IdentityManagementTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void identityListEmptyIfNoIdentitiesExist() {
        // Espresso doesn't seem to have an easy way to retrieve the list of spinner text
        // So we do this manually instead
        Activity mainActivity = mActivityRule.getActivity();
        Spinner identitySpinner = (Spinner)mainActivity.findViewById(R.id.IdentitySpinner);
        SpinnerAdapter identitySpinnerAdapter = identitySpinner.getAdapter();
        assertNotNull(identitySpinnerAdapter);
        assertEquals(0, identitySpinnerAdapter.getCount());
    }
}
