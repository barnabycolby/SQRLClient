package io.barnabycolby.sqrlclient.tasks;

/**
 * A listener for alert dialogs with two possible outcomes.
 */
public interface IdentRequestListener {
    public void abortIdentRequest();
    public void proceedWithIdentRequest();
}
