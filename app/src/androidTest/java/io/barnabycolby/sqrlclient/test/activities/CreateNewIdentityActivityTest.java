package io.barnabycolby.sqrlclient.test.activities;

import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.R;

public class CreateNewIdentityActivityTest extends ActivityInstrumentationTestCase2<CreateNewIdentityActivity> {
    public CreateNewIdentityActivityTest() {
        super(CreateNewIdentityActivity.class);
    }

    public void testExplanationTextIsDisplayed() {
        // Get the activity
        CreateNewIdentityActivity activity = getActivity();

        // Get the expected explanation text
        Resources resources = activity.getResources();
        String expected = resources.getString(R.string.create_new_identity_explanation);

        // Assert that the appropriate TextView is visible and shows the correct text
        TextView explanationTextView = (TextView)activity.findViewById(R.id.CreateNewIdentityExplanationTextView);
        assertEquals(expected, explanationTextView.getText());
        assertEquals(View.VISIBLE, explanationTextView.getVisibility());
    }

    public void testProgressBarIsDisplayed() {
        // Get the activity
        CreateNewIdentityActivity activity = getActivity();

        // Find the progress bar and make sure it's visible
        ProgressBar progressBar = (ProgressBar)activity.findViewById(R.id.EntropyHarvesterProgressBar);
        assertNotNull(progressBar);
        assertEquals(View.VISIBLE, progressBar.getVisibility());
    }
}
