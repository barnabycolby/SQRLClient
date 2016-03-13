package io.barnabycolby.sqrlclient.helpers;

import io.barnabycolby.sqrlclient.helpers.DetachableListener.DetachableListenerInterface;
import io.barnabycolby.sqrlclient.sqrl.DecryptIdentityListener;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;

/**
 * This interface is used in the creation of a detachable DecryptIdentityListener using the DetachableListener class.
 */
public interface DecryptIdentityDetachableListener extends DecryptIdentityListener, DetachableListenerInterface {
    @Override public void attach(Object listener);
    @Override public void detach();

    @Override public void onIdentityDecrypted(SQRLIdentity result);
    @Override public void onIdentityDecryptionProgressUpdate(int progress);
}
