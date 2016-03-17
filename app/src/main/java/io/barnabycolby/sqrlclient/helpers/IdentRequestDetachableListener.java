package io.barnabycolby.sqrlclient.helpers;

import io.barnabycolby.sqrlclient.helpers.DetachableListener.DetachableListenerInterface;

/**
 * This interface is used in the creation of a detachable IdentRequestListener using the DetachableListener class.
 */
public interface IdentRequestDetachableListener extends IdentRequestListener, DetachableListenerInterface {
    @Override public void attach(Object listener);
    @Override public void detach();

    @Override public void onIdentRequestFinished();
}
