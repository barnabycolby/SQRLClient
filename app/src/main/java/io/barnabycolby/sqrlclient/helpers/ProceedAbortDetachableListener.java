package io.barnabycolby.sqrlclient.helpers;

import io.barnabycolby.sqrlclient.helpers.DetachableListener.DetachableListenerInterface;

/**
 * This interface is used in the creation of a detachable ProceedAbortListener using the DetachableListener class.
 */
public interface ProceedAbortDetachableListener extends ProceedAbortListener, DetachableListenerInterface {
    @Override public void attach(Object listener);
    @Override public void detach();

    @Override public void proceed();
    @Override public void abort();
}
