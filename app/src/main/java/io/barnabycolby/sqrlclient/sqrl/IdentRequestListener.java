package io.barnabycolby.sqrlclient.sqrl;

/**
 * A listener for alert dialogs with two possible outcomes.
 */
public interface IdentRequestListener {
    public void abortIdentRequest();
    public void proceedWithIdentRequest();
}
