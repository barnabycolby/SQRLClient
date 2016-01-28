package io.barnabycolby.sqrlclient.sqrl;

import android.support.v4.util.SimpleArrayMap;

import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;

/**
 * Manages the SQRL Identities of the application, including their persistence across application instances.
 */
public class SQRLIdentityManager {
    /**
     * Stores the runtime object containing the identities, which is simply a mapping from identity name to master key.
     */
    private static SimpleArrayMap<String, byte[]> sIdentities;
    static {
        sIdentities = new SimpleArrayMap<String, byte[]>();
    }

    /**
     * Saves a new identity to the system.
     *
     * @param identityName  The name of the new identity. This will be used for UI identification and system identification.
     * @param masterKey  The master key of the new identity.
     * @throws IdentityAlreadyExistsException  If an identity with the same name already exists.
     */
    public static void save(String identityName, byte[] masterKey) throws IdentityAlreadyExistsException {
        if (sIdentities.containsKey(identityName)) {
            throw new IdentityAlreadyExistsException();
        }

        sIdentities.put(identityName, masterKey);
    }
}
