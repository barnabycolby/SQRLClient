package io.barnabycolby.sqrlclient.activities;

import android.app.Fragment;
import android.os.Bundle;

import io.barnabycolby.sqrlclient.helpers.PasswordCryptDetachableListener;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;
import io.barnabycolby.sqrlclient.tasks.PasswordVerificationTask;

/**
 * Stores the state required by the EnterPassword Activity, allowing it to recover after a runtime change, such as an orientation change.
 *
 * Includes help for attaching/detaching the listeners used by the activity.
 */
public class EnterPasswordStateFragment extends Fragment {
    private PasswordCryptDetachableListener mPasswordCryptDetachableListener;
    private PasswordVerificationTask mPasswordVerificationTask;

    /**
     * Constructs a new instance using the given objects.
     *
     * @param passwordCryptDetachableListener  The listener object used for password verification.
     */
    public EnterPasswordStateFragment(PasswordCryptListener passwordCryptListener) {
        this.mPasswordCryptDetachableListener = new PasswordCryptDetachableListener(passwordCryptListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // As this fragment stores state, it must not be destroyed when the activity it belongs too is destroyed
        setRetainInstance(true);
    }

    /**
     * Returns the detachable listener used for password verification results.
     */
    public PasswordCryptDetachableListener getPasswordCryptDetachableListener() {
        return this.mPasswordCryptDetachableListener;
    }

    public PasswordVerificationTask getPasswordVerificationTask() {
        return this.mPasswordVerificationTask;
    }

    public void setPasswordVerificationTask(PasswordVerificationTask passwordVerificationTask) {
        this.mPasswordVerificationTask = passwordVerificationTask;
    }
}
