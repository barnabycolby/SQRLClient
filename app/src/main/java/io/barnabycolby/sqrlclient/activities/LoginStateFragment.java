package io.barnabycolby.sqrlclient.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.helpers.ProceedAbortListener;
import io.barnabycolby.sqrlclient.helpers.ProceedAbortDetachableListener;
import io.barnabycolby.sqrlclient.helpers.SwappableTextView;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLUri;

/**
 * Stores the state required by the Login Activity, allowing it to recover after a runtime change, such as an orientation change.
 *
 * Includes help for attaching/detaching the listeners used by the activity.
 */
public class LoginStateFragment extends Fragment {
    private SwappableTextView mInformationTextView;
    private SQRLUri mSQRLUri;
    private SQRLRequestFactory mRequestFactory;
    private ProceedAbortDetachableListener mAccountExistsDetachableListener;
    private ProceedAbortDetachableListener mDialogDetachableListener;
    private String mDisplayName;

    /**
     * Constructs a new instance using the given objects.
     *
     * @param informationTextView  The information text view object to retain.
     * @param sqrlUri  The SQRLUri object to retain.
     * @param requestFactory  The SQRLRequestFactory object to retain.
     * @param accountExistsListener  The listener object used for account exists callbacks.
     * @param dialogListener  The listener object used for create account dialog callbacks.
     * @param displayName  The servers display name.
     */
    public LoginStateFragment(SwappableTextView informationTextView, SQRLUri sqrlUri, SQRLRequestFactory requestFactory, ProceedAbortListener accountExistsListener, ProceedAbortListener dialogListener, String displayName) {
        this.mInformationTextView = informationTextView;
        this.mSQRLUri = sqrlUri;
        this.mRequestFactory = requestFactory;
        this.mAccountExistsDetachableListener = new ProceedAbortDetachableListener(accountExistsListener);
        this.mDialogDetachableListener = new ProceedAbortDetachableListener(dialogListener);
        this.mDisplayName = displayName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // As this fragment stores state, it must not be destroyed when the activity it belongs too is destroyed
        setRetainInstance(true);
    }

    /**
     * Gets the retained information TextView.
     *
     * @return The retained information TextView.
     */
    public SwappableTextView getInformationTextView() {
        return this.mInformationTextView;
    }

    /**
     * Stores a new information TextView to replace the existing retained information TextView.
     *
     * @param informationTextView  The new TextView to retain.
     */
    public void setInformationTextView(SwappableTextView informationTextView) {
        this.mInformationTextView = informationTextView;
    }

    /**
     * Gets the retained SQRLUri.
     *
     * @return The retained SQRLUri.
     */
    public SQRLUri getSQRLUri() {
        return this.mSQRLUri;
    }

    /**
     * Returns the detachable listener used for account exists callbacks.
     */
    public ProceedAbortDetachableListener getAccountExistsDetachableListener() {
        return this.mAccountExistsDetachableListener;
    }

    /**
     * Returns the detachable listener used for create account dialog callbacks.
     */
    public ProceedAbortDetachableListener getDialogDetachableListener() {
        return this.mDialogDetachableListener;
    }

    /**
     * Gets the retained SQRLRequestFactory.
     */
    public SQRLRequestFactory getRequestFactory() {
        return this.mRequestFactory;
    }

    /**
     * Gets the retained server display name.
     */
    public String getDisplayName() {
        return this.mDisplayName;
    } 
}
