package io.barnabycolby.sqrlclient.helpers;

import android.widget.TextView;

/**
 * A wrapper around TextView that allows the underlying TextView object to be silently swapped without the user of this objects knowledge.
 *
 * In the future it may be necessary to come up with a better implementation that automatically delegates TextView method calls to the underlying
 * TextView. Something like a dynamic proxy would work here. For now, it is better to manually delegate the small amount of required method calls,
 * and update the API signatures of the required classes (extending TextView would prevent this) than to take the performance hit of reflection.
 */
public class SwappableTextView {
    private TextView mRawTextView;

    /**
     * Constructs a new instance that wraps the given TextView.
     *
     * @param rawTextView  The text view to wrap.
     */
    public SwappableTextView(TextView rawTextView) {
        this.mRawTextView = rawTextView;
    }

    /**
     * @see android.widget.TextView#setText
     */
    public void setText(String textToSet) {
        this.mRawTextView.setText(textToSet);
    }

    /**
     * @see android.widget.TextView#getText
     */
    public String getText() {
        return this.mRawTextView.getText().toString();
    }

    /**
     * @see android.widget.TextView#setVisibility
     */
    public void setVisibility(int visibility) {
        this.mRawTextView.setVisibility(visibility);
    }

    /**
     * Swaps the underlying TextView with a new TextView.
     *
     * @param newTextView  The new underlying TextView.
     */
    public void setTextView(TextView newTextView) {
        newTextView.setText(this.mRawTextView.getText());
        this.mRawTextView = newTextView;
    }
}
