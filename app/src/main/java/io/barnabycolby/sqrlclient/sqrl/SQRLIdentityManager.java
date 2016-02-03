package io.barnabycolby.sqrlclient.sqrl;

import android.support.v4.util.SimpleArrayMap;

import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.App;

/**
 * Manages the SQRL Identities of the application, including their persistence across application instances.
 */
public class SQRLIdentityManager {
    /**
     * Stores the runtime object containing the identities, which is simply a mapping from identity name to master key.
     */
    private SimpleArrayMap<String, byte[]> mIdentities;

    /**
     * Constructs a new instance of the SQRLIdentityManager.
     *
     * Handles the loading of identities from disk.
     */
    public SQRLIdentityManager() {
        mIdentities = new SimpleArrayMap<String, byte[]>();
    }

    /**
     * Saves a new identity to the system.
     *
     * @param identityName  The name of the new identity. This will be used for UI identification and system identification.
     * @param masterKey  The master key of the new identity.
     * @throws IdentityAlreadyExistsException  If an identity with the same name already exists.
     */
    public void save(String identityName, byte[] masterKey) throws IdentityAlreadyExistsException {
        if (mIdentities.containsKey(identityName)) {
            throw new IdentityAlreadyExistsException();
        }

        mIdentities.put(identityName, masterKey);
    }
}
