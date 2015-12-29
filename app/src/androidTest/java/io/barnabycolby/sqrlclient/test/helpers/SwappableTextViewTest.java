package io.barnabycolby.sqrlclient.test.helpers;

import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.helpers.SwappableTextView;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class SwappableTextViewTest {

    @Test
    public void delegateTextViewMethodCallsToTextView() throws Exception {
        // Create the mock raw text view
        TextView rawTextView = mock(TextView.class);
        String testString = "beans";
        when(rawTextView.getText()).thenReturn(testString);

        // Perform the test
        SwappableTextView swappableTextView = new SwappableTextView(rawTextView);
        swappableTextView.setText(testString);
        verify(rawTextView).setText(testString);
        String actual = swappableTextView.getText();
        verify(rawTextView).getText();
        Assert.assertEquals(testString, actual);
        swappableTextView.setVisibility(View.INVISIBLE);
        verify(rawTextView).setVisibility(View.INVISIBLE);
    }

    @Test
    public void setTextViewShouldChangeUnderlyingTextView() throws Exception {
        // Prepare the mocks
        TextView originalTextView = mock(TextView.class);
        String testString = "sausages";
        when(originalTextView.getText()).thenReturn(testString);
        TextView newTextView = mock(TextView.class);

        // Check that the currently set text is transferred to the new text view
        SwappableTextView swappableTextView = new SwappableTextView(originalTextView);
        swappableTextView.setText(testString);
        verify(originalTextView).setText(testString);
        swappableTextView.setTextView(newTextView);
        verify(newTextView).setText(testString);

        // Check that subsequent calls to delegated methods are applied to the new text view
        String newTestString = "ham";
        swappableTextView.setText(newTestString);
        verify(newTextView).setText(newTestString);
    }
}
