package io.barnabycolby.sqrlclient.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import io.barnabycolby.sqrlclient.helpers.ProceedAbortListener;
import io.barnabycolby.sqrlclient.R;

/**
 * Dialog that asks the user whether they would like to create an account for a website.
 */
public class CreateAccountDialogFragment extends DialogFragment {
    private ProceedAbortListener mListener;

    /**
     * Constructs an instance of the fragment.
     *
     * @param listener  The listener that implements the callbacks called when the dialog is dismissed.
     */
    public CreateAccountDialogFragment(ProceedAbortListener listener) {
        this.mListener = listener;
        this.setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setMessage(R.string.dialog_create_account)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mListener.proceed();
                }
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mListener.abort();
                }
            });
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();

        // Workaround for bug: http://code.google.com/p/android/issues/detail?id=17423
        if ((dialog != null) && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }

        super.onDestroyView();
    }
}
