package io.barnabycolby.sqrlclient.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;

import io.barnabycolby.sqrlclient.helpers.DetachableListener;
import io.barnabycolby.sqrlclient.helpers.PasswordCryptDetachableListener;
import io.barnabycolby.sqrlclient.sqrl.PasswordCryptListener;
import io.barnabycolby.sqrlclient.tasks.DecryptIdentityTask;

/**
 * Stores the state required by the EnterPassword Activity, allowing it to recover after a runtime change, such as an orientation change.
 *
 * Includes help for attaching/detaching the listeners used by the activity.
 */
public class EnterPasswordStateFragment extends Fragment {
    private PasswordCryptDetachableListener mPasswordCryptDetachableListener;
    private DecryptIdentityTask mDecryptIdentityTask;

    /**
     * Constructs a new instance using the given objects.
     *
     * @param passwordCryptListener  The listener object used for password verification.
     */
    public EnterPasswordStateFragment(PasswordCryptListener passwordCryptListener) {
        this.mPasswordCryptDetachableListener = (PasswordCryptDetachableListener)DetachableListener.create(passwordCryptListener, PasswordCryptDetachableListener.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // As this fragment stores state, it must not be destroyed when the activity it belongs too is destroyed
        setRetainInstance(true);
    }

    /**
     * Returns the detachable listener used for password verification results.
     *
     * @return The detachable listener used for password verification results.
     */
    public PasswordCryptDetachableListener getPasswordCryptDetachableListener() {
        return this.mPasswordCryptDetachableListener;
    }

    /**
     * Gets the task used for decrypting the identity.
     *
     * @return The task used for decrypting the identity.
     */
    public DecryptIdentityTask getDecryptIdentityTask() {
        return this.mDecryptIdentityTask;
    }

    /**
     * Stores the given DecryptIdentityTask.
     *
     * @param decryptIdentityTask  The task to store.
     */
    public void setDecryptIdentityTask(DecryptIdentityTask decryptIdentityTask) {
        this.mDecryptIdentityTask = decryptIdentityTask;
    }
}
