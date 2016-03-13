package io.barnabycolby.sqrlclient.helpers;

import io.barnabycolby.sqrlclient.helpers.DetachableListener.DetachableListenerInterface;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;
import io.barnabycolby.sqrlclient.sqrl.SQRLIdentity;

/**
 * This interface is used in the creation of a detachable PasswordCryptListener using the DetachableListener class.
 */
public interface PasswordCryptDetachableListener extends PasswordCryptListener, DetachableListenerInterface {
    @Override public void attach(Object listener);
    @Override public void detach();

    @Override public void onPasswordCryptResult(boolean result);
    @Override public void onPasswordCryptProgressUpdate(int progress);
}
