package io.barnabycolby.sqrlclient.dialogs;

import android.support.v4.app.FragmentManager;

import io.barnabycolby.sqrlclient.tasks.IdentRequestListener;

/**
 * Creates and displays a dialog that asks the user whether or not a new account should be created.
 */
public class CreateAccountDialogFactory {
    private IdentRequestListener mListener;
    private FragmentManager mFragmentManager;

    /**
     * Constructs a new instance of the factory.
     *
     * @param listener  The listener that implements the callbacks that are called when the user responds to the dialog.
     * @param fragmentManager  The fragment manager used to display the dialog.
     */
    public CreateAccountDialogFactory(IdentRequestListener listener, FragmentManager fragmentManager) {
        this.mListener = listener;
        this.mFragmentManager = fragmentManager;
    }

    /**
     * Creates and displays the dialog.
     */
    public void create() {
        CreateAccountDialogFragment dialogFragment = new CreateAccountDialogFragment(mListener);
        dialogFragment.show(mFragmentManager, "createAccount");
    }
}
