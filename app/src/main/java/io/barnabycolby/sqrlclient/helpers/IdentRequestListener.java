package io.barnabycolby.sqrlclient.helpers;

/**
 * This listener interface should be used to receive a notification when the IdentRequestTask has finished.
 */
public interface IdentRequestListener {
    /**
     * Called when the ident request task has finished.
     */
    public void onIdentRequestFinished();
}
