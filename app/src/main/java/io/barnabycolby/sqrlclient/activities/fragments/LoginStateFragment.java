package io.barnabycolby.sqrlclient.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.TextView;

import io.barnabycolby.sqrlclient.helpers.DetachableListener;
import io.barnabycolby.sqrlclient.helpers.IdentRequestListener;
import io.barnabycolby.sqrlclient.helpers.IdentRequestDetachableListener;
import io.barnabycolby.sqrlclient.helpers.ProceedAbortListener;
import io.barnabycolby.sqrlclient.helpers.ProceedAbortDetachableListener;
import io.barnabycolby.sqrlclient.helpers.SwappableTextView;
import io.barnabycolby.sqrlclient.sqrl.factories.SQRLRequestFactory;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;

/**
 * Stores the state required by the Login Activity, allowing it to recover after a runtime change, such as an orientation change.
 *
 * Includes help for attaching/detaching the listeners used by the activity.
 */
public class LoginStateFragment extends Fragment {
    private SwappableTextView mInformationTextView;
    private SQRLIdentity mSQRLIdentity;
    private SQRLRequestFactory mRequestFactory;
    private ProceedAbortDetachableListener mAccountExistsDetachableListener;
    private ProceedAbortDetachableListener mDialogDetachableListener;
    private IdentRequestDetachableListener mIdentRequestDetachableListener;
    private String mDisplayName;

    /**
     * Constructs a new instance using the given objects.
     *
     * @param informationTextView  The information text view object to retain.
     * @param sqrlUri  The SQRLIdentity object to retain.
     * @param requestFactory  The SQRLRequestFactory object to retain.
     * @param accountExistsListener  The listener object used for account exists callbacks.
     * @param dialogListener  The listener object used for create account dialog callbacks.
     * @param identRequestListener  The listener object used for ident request callbacks.
     * @param displayName  The servers display name.
     */
    public LoginStateFragment(SwappableTextView informationTextView, SQRLIdentity sqrlIdentity, SQRLRequestFactory requestFactory, ProceedAbortListener accountExistsListener, ProceedAbortListener dialogListener, IdentRequestListener identRequestListener, String displayName) {
        this.mInformationTextView = informationTextView;
        this.mSQRLIdentity = sqrlIdentity;
        this.mRequestFactory = requestFactory;
        this.mAccountExistsDetachableListener = (ProceedAbortDetachableListener)DetachableListener.create(accountExistsListener, ProceedAbortDetachableListener.class);
        this.mDialogDetachableListener = (ProceedAbortDetachableListener)DetachableListener.create(dialogListener, ProceedAbortDetachableListener.class);
        this.mIdentRequestDetachableListener = (IdentRequestDetachableListener)DetachableListener.create(identRequestListener, IdentRequestDetachableListener.class);
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
     * Gets the retained SQRLIdentity.
     *
     * @return The retained SQRLIdentity.
     */
    public SQRLIdentity getSQRLIdentity() {
        return this.mSQRLIdentity;
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
     * Returns the detachable listener used for ident request callbacks.
     */
    public IdentRequestDetachableListener getIdentRequestDetachableListener() {
        return this.mIdentRequestDetachableListener;
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
