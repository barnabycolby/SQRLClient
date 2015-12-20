package io.barnabycolby.sqrlclient.helpers;

/**
 * A listener for alert dialogs with two possible outcomes.
 */
public interface AlertDialogListener {
    public void onDialogPositiveClick();
    public void onDialogNegativeClick();
}
