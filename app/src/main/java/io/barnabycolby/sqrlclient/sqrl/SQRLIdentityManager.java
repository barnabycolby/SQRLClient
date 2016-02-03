package io.barnabycolby.sqrlclient.sqrl;

import android.support.v4.util.SimpleArrayMap;
import android.util.Base64;
import android.util.Log;

import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeWrittenToDiskException;
import io.barnabycolby.sqrlclient.App;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Manages the SQRL Identities of the application, including their persistence across application instances.
 */
public class SQRLIdentityManager {
    private static final String TAG = SQRLIdentityManager.class.getName();

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
        File identitiesFolder = openIdentitiesFolder();

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
     * Opens a handle to the identities folder stored in internal storage, creating it if necessary.
     */
    private File openIdentitiesFolder() throws IdentitiesCouldNotBeLoadedException {
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

        return identitiesFolder;
    }

    /**
     * Saves a new identity to the system.
     *
     * @param identityName  The name of the new identity. This will be used for UI identification and system identification.
     * @param masterKey  The master key of the new identity.
     * @throws IdentityAlreadyExistsException  If an identity with the same name already exists.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be opened.
     */
    public void save(String identityName, byte[] masterKey) throws IdentityAlreadyExistsException, IdentityCouldNotBeWrittenToDiskException, IdentitiesCouldNotBeLoadedException {
        if (mIdentities.containsKey(identityName)) {
            throw new IdentityAlreadyExistsException();
        }

        // We write it to disk before adding it to the runtime array in case the writeNewIdentityToDisk call throws an exception
        try {
            writeNewIdentityToDisk(identityName, masterKey);
        } catch (IOException ex) {
            Log.e(TAG, "Could not write new identity to disk: " + ex.getMessage());
            throw new IdentityCouldNotBeWrittenToDiskException();
        }
        mIdentities.put(identityName, masterKey);
    }

    /**
     * Writes a new identity to the internal file storage.
     *
     * @param identityName  The name of the new identity. This will be used for UI identification and system identification.
     * @param masterKey  The master key of the new identity.
     * @throws IdentityAlreadyExistsException  If an identity with the same name already exists.
     * @throws IOException  If an IO error occurred.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be opened.
     */
    private void writeNewIdentityToDisk(String identityName, byte[] masterKey) throws IdentityAlreadyExistsException, IdentitiesCouldNotBeLoadedException, IOException {
        // First, convert the filename to it's base64url representation. This should remove a potential filename attack surface.
        byte[] identityNameAsByteArray = identityName.getBytes(Charset.forName("UTF-8"));
        String encodedFilename = Base64.encodeToString(identityNameAsByteArray, Base64.NO_PADDING | Base64.URL_SAFE | Base64.NO_WRAP);

        // Create the new file
        File identitiesFolder = openIdentitiesFolder();
        File newIdentityFile = new File(identitiesFolder, encodedFilename);
        if (newIdentityFile.exists()) {
            throw new IdentityAlreadyExistsException();
        }
        newIdentityFile.createNewFile();

        // Write the master key to the file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newIdentityFile));
        bufferedOutputStream.write(masterKey);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    /**
     * Gets the list of identity names.
     *
     * @return A list of identity names.
     */
    public List<String> getIdentityNames() {
        int numberOfIdentities = this.mIdentities.size();
        ArrayList<String> identityNames = new ArrayList<String>(numberOfIdentities);
        for (int i = 0; i < numberOfIdentities; i++) {
            identityNames.add(this.mIdentities.keyAt(i));
        }
        return identityNames;
    }
}
