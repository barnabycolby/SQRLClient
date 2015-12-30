package io.barnabycolby.sqrlclient.helpers;

/**
 * Wrapper around ProceedAbortListener that allows the listener to be detached and reattached without the listener clients knowledge.
 *
 * During the time where no listener is attached, if a call to proceed or abort occurs it will be logged and replayed to the next listener that attaches.
 */
public class ProceedAbortDetachableListener implements ProceedAbortListener {
    private ProceedAbortListener mListener;
    private boolean mProceedCalledDuringDetach = false;
    private boolean mAbortCalledDuringDetach = false;

    /**
     * Constructs a new instance in the detached listener state.
     */
    public ProceedAbortDetachableListener() {}

    /**
     * Constructs a new instance using the given listener.
     *
     * @param listener  The listener to use for handling incoming proceed and abort calls.
     */
    public ProceedAbortDetachableListener(ProceedAbortListener listener) {
        this.mListener = listener;
    }

    /**
     * Attaches a new listener, causing the old one to be replaced if it exists.
     *
     * If this was in a detached state before the call to attach, any proceed or abort calls made during this time of detachment will be replayed
     * to the given listener.
     *
     * @param listener  The new listener.
     */
    public void attach(ProceedAbortListener listener) {
        this.mListener = listener;

        if (this.mProceedCalledDuringDetach) {
            this.mListener.proceed();
            this.mProceedCalledDuringDetach = false;
        }

        if (this.mAbortCalledDuringDetach) {
            this.mListener.abort();
            this.mAbortCalledDuringDetach = false;
        }
    }

    /**
     * Detaches the listener, if one is attached.
     */
    public void detach() {
        this.mListener = null;
    }

    @Override
    public void proceed() {
        if (this.mListener != null) {
            this.mListener.proceed();
        } else {
            this.mProceedCalledDuringDetach = true;
        }
    }

    @Override
    public void abort() {
        if (this.mListener != null) {
            this.mListener.abort();
        } else {
            this.mAbortCalledDuringDetach = true;
        }
    }
}
