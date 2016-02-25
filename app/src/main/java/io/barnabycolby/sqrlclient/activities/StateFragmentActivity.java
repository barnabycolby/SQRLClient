package io.barnabycolby.sqrlclient.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Handles an activities state if it must persist across activity instances.
 *
 * <p>
 * This activity might be used if an activity wants to persist objects across activity instances (such as orientation changes) that onSaveInstance
 * bundles cannot handle. It is parameterised by the Fragment type.
 * </p>
 *
 * <p>
 * The user must ensure that initialiseFragment is called before any attempts to use the fragment are made. Usually it should be called within
 * onCreate.
 * </p>
 */
public abstract class StateFragmentActivity<T extends Fragment> extends AppCompatActivity {
    private String mStateFragmentTag = "stateFragment";
    protected T mStateFragment;

    /**
     * Retrieves the fragment if it already exists, calling initialise or restore during the process.
     *
     * This method should be called before attempting to use the fragment, and is usually called during onCreate.
     */
    @SuppressWarnings("unchecked")
    protected void initialiseFragment() {
        FragmentManager fragmentManager = this.getFragmentManager();
        Fragment stateFragmentBeforeCast = fragmentManager.findFragmentByTag(mStateFragmentTag);
        if (stateFragmentBeforeCast == null) {
            this.mStateFragment = initialise();
            if (this.mStateFragment != null) {
                fragmentManager.beginTransaction().add(this.mStateFragment, this.mStateFragmentTag).commit();
            }
        } else {
            this.mStateFragment = (T)stateFragmentBeforeCast;
            restore();
        }
    }

    /**
     * Called if the fragment does not already exist, indicating that this is the first instance of the activity.
     *
     * @return The new fragment.
     */
    protected abstract T initialise();

    /**
     * Called if the fragment was found and restored.
     */
    protected abstract void restore();
}
