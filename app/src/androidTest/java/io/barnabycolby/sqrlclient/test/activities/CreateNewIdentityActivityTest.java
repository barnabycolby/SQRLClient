package io.barnabycolby.sqrlclient.test.activities;

import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.activities.CreateNewIdentityActivity;
import io.barnabycolby.sqrlclient.R;

public class CreateNewIdentityActivityTest extends ActivityInstrumentationTestCase2<CreateNewIdentityActivity> {
    private CreateNewIdentityActivity mActivity;

    public CreateNewIdentityActivityTest() {
        super(CreateNewIdentityActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.mActivity = getActivity();
    }

    public void testExplanationTextIsDisplayed() {
        // Get the expected explanation text
        Resources resources = mActivity.getResources();
        String expected = resources.getString(R.string.create_new_identity_explanation);

        // Assert that the appropriate TextView is visible and shows the correct text
        TextView explanationTextView = (TextView)mActivity.findViewById(R.id.CreateNewIdentityExplanationTextView);
        assertEquals(expected, explanationTextView.getText());
        assertEquals(View.VISIBLE, explanationTextView.getVisibility());
    }

    public void testProgressBarIsDisplayed() {
        // Find the progress bar and make sure it's visible
        ProgressBar progressBar = (ProgressBar)mActivity.findViewById(R.id.EntropyHarvesterProgressBar);
        assertNotNull(progressBar);
        assertEquals(View.VISIBLE, progressBar.getVisibility());
    }

    public void testIdentityNameEditTextIsDisplayed() {
        // Find the textbox and make sure it's visible
        EditText identityNameEditText = (EditText)mActivity.findViewById(R.id.IdentityNameEditText); 
        assertNotNull(identityNameEditText);
        assertEquals(View.VISIBLE, identityNameEditText.getVisibility());
    }
}
