package io.barnabycolby.sqrlclient.helpers;

import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;

/**
 * Wrapper around PasswordCryptListener that allows the listener to be detached and reattached without the listener clients knowledge.
 *
 * During the time where no listener is attached, if a call to onPasswordCryptResult or onPasswordCryptProgressUpdate occurs it will be logged and replayed to the next listener that attaches.
 */
public class PasswordCryptDetachableListener implements PasswordCryptListener {
    private PasswordCryptListener mListener;
    private boolean mResultCalledDuringDetach = false;
    private boolean mResultValue;
    private boolean mProgressUpdateCalledDuringDetach = false;
    private int mProgressUpdateValue;

    /**
     * Constructs a new instance in the detached listener state.
     */
    public PasswordCryptDetachableListener() {}

    /**
     * Constructs a new instance using the given listener.
     *
     * @param listener  The listener to use for handling incoming listener calls.
     */
    public PasswordCryptDetachableListener(PasswordCryptListener listener) {
        this.mListener = listener;
    }

    /**
     * Attaches a new listener, causing the old one to be replaced if it exists.
     *
     * If this was in a detached state before the call to attach, any listener calls made during this time of detachment will be replayed
     * to the given listener.
     *
     * @param listener  The new listener.
     */
    public void attach(PasswordCryptListener listener) {
        this.mListener = listener;

        if (this.mResultCalledDuringDetach) {
            this.mListener.onPasswordCryptResult(this.mResultValue);
            this.mResultCalledDuringDetach = false;
        }

        if (this.mProgressUpdateCalledDuringDetach) {
            this.mListener.onPasswordCryptProgressUpdate(this.mProgressUpdateValue);
            this.mProgressUpdateCalledDuringDetach = false;
        }
    }

    /**
     * Detaches the listener, if one is attached.
     */
    public void detach() {
        this.mListener = null;
    }

    @Override
    public void onPasswordCryptResult(boolean result) {
        if (this.mListener != null) {
            this.mListener.onPasswordCryptResult(result);
        } else {
            this.mResultCalledDuringDetach = true;
            this.mResultValue = result;
        }
    }

    @Override
    public void onPasswordCryptProgressUpdate(int progress) {
        if (this.mListener != null) {
            this.mListener.onPasswordCryptProgressUpdate(progress);
        } else {
            this.mProgressUpdateCalledDuringDetach = true;
            this.mProgressUpdateValue = progress;
        }
    }
}
