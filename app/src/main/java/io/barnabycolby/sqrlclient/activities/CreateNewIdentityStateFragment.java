package io.barnabycolby.sqrlclient.activities;

import android.app.Fragment;
import android.os.Bundle;

import io.barnabycolby.sqrlclient.sqrl.EntropyCollector;

/**
 * Stores the state required by the CreateNewIdentity Activity, allowing it to recover after a runtime change, such as an orientation change.
 */
public class CreateNewIdentityStateFragment extends Fragment {
    private EntropyCollector mEntropyCollector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // As this fragment stores state, it must not be destroyed when the activity it belongs too is destroyed
        setRetainInstance(true);
    }

    /**
     * Gets the retained entropy collector.
     *
     * @return The entropy collector.
     */
    public EntropyCollector getEntropyCollector() {
        return this.mEntropyCollector;
    }

    /**
     * Sets the entropy collector to retain
     *
     * @param entropyCollector  The entropy collector.
     */
    public void setEntropyCollector(EntropyCollector entropyCollector) {
        this.mEntropyCollector = entropyCollector;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mEntropyCollector.close();
    }
}
