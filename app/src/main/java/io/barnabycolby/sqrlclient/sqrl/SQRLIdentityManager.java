package io.barnabycolby.sqrlclient.sqrl;

import android.support.v4.util.SimpleArrayMap;
import android.util.Base64;

import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.App;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

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
     * 
     * @throws IdentitiesCouldNotBeLoadedException  If the identities could not be loaded from disk.
     */
    public SQRLIdentityManager() throws IdentitiesCouldNotBeLoadedException {
        mIdentities = new SimpleArrayMap<String, byte[]>();

        // Open the identities folder
        File internalStorage = App.getContext().getFilesDir();
        File identitiesFolder = new File(internalStorage, "identities");
        if (identitiesFolder.exists()) {
            if (identitiesFolder.isFile()) {
                // Not much we can do here
                // If this happens then it's definitely an exceptional circumstance
                throw new IdentitiesCouldNotBeLoadedException();
            }
        } else {
            boolean result = identitiesFolder.mkdir();
            if (!result) {
                throw new IdentitiesCouldNotBeLoadedException();
            }
        }

        // Loop over the identities, adding them if valid
        for (File identityFile : identitiesFolder.listFiles()) {
            // Retrieve the master key and check it is the correct length
            byte[] masterKey;
            try {
                masterKey = FileUtils.readFileToByteArray(identityFile);
            } catch (IOException ex) {
                continue;
            }
            if (masterKey.length != 32) {
                continue;
            }

            // Convert the filename to a human readable string (it should be in base64url format)
            byte[] decodedIdentityName;
            try {
                decodedIdentityName = Base64.decode(identityFile.getName(), Base64.URL_SAFE);
            } catch (IllegalArgumentException ex) {
                // Bad base64
                continue;
            }
            String identityName = new String(decodedIdentityName, Charset.forName("UTF-8"));

            // Add the identity to the map
            mIdentities.put(identityName, masterKey);
        }
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
