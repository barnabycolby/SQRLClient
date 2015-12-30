package io.barnabycolby.sqrlclient.tasks;

/**
 * A listener used to either proceed with or abort an Ident request.
 */
public interface IdentRequestListener {
    /**
     * Called when the ident request should be aborted.
     */
    public void abortIdentRequest();

    /**
     * Called when the ident request should proceed.
     */
    public void proceedWithIdentRequest();
}
