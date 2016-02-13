package io.barnabycolby.sqrlclient.sqrl;

import android.support.v4.util.SimpleArrayMap;
import android.util.Base64;
import android.util.Log;

import io.barnabycolby.sqrlclient.exceptions.CryptographyException;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.exceptions.IdentityDoesNotExistException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeWrittenToDiskException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeDeletedException;
import io.barnabycolby.sqrlclient.exceptions.InvalidMasterKeyException;
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
     * Stores the currently selected identity, that should be used when creating SQRLIdentity objects.
     */
    private String mCurrentIdentity;

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
            String identityName;
            try {
                identityName = base64DecodeFilename(identityFile);
            } catch (IllegalArgumentException ex) {
                // Bad base64
                continue;
            }

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
        // Create the new file
        String encodedFilename = base64Encode(identityName);
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
     * Base64Url encodes a string.
     *
     * @param plainText  The plaintext string to encode.
     * @return  The encoded string.
     */
    private String base64Encode(String plainText) {
        byte[] plainTextAsByteArray = plainText.getBytes(Charset.forName("UTF-8"));
        return Base64.encodeToString(plainTextAsByteArray, Base64.NO_PADDING | Base64.URL_SAFE | Base64.NO_WRAP);
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

    /**
     * Removes all identities from the list.
     *
     * @throws IOException  If an identity could not be deleted.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities files could not be loaded.
     * @throws IdentityCouldNotBeDeletedException  If the identity file on disk could not be deleted.
     */
    public void removeAllIdentities() throws IOException, IdentitiesCouldNotBeLoadedException, IdentityCouldNotBeDeletedException {
        File identitiesFolder = openIdentitiesFolder();
        // We need to iterate in reverse order as the removeIdentity call will remove the item from the list
        for (int i = this.mIdentities.size() - 1; i >= 0; i--) {
            String identityName = this.mIdentities.keyAt(i);
            try {
                removeIdentity(identityName, identitiesFolder);
            } catch (IdentityDoesNotExistException ex) {
                // We can ignore this case as we have already verified that the identity does exist
                // But just in case, we can log a wtf error
                Log.wtf(TAG, "Calling removeIdentity from removeAllIdentities threw a IdentityDoesNotExistException.");
            }
        }
    }

    /**
     * Removes a single identity from the list.
     *
     * @param identityName  The name of the identity to remove.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be loaded.
     * @throws IdentityDoesNotExistException  If the identity given by the identity name does not exist.
     * @throws IdentityCouldNotBeDeletedException  If the identity file on disk could not be deleted.
     */
    public void removeIdentity(String identityName) throws IdentitiesCouldNotBeLoadedException, IdentityDoesNotExistException, IdentityCouldNotBeDeletedException {
        removeIdentity(identityName, openIdentitiesFolder());
    }

    /**
     * Removes a single identity from the list.
     *
     * Passing the identities folder file in, prevents this method from having to retrieve this for each identity.
     *
     * @param identityName  The name of the identity to remove.
     * @throws IdentityDoesNotExistException  If the identity given by the identity name does not exist.
     * @throws IdentityCouldNotBeDeletedException  If the identity file on disk could not be deleted.
     */
    public void removeIdentity(String identityName, File identitiesFolder) throws IdentityDoesNotExistException, IdentityCouldNotBeDeletedException {
        // Check that the given identityName is valid
        if (!this.mIdentities.containsKey(identityName)) {
            throw new IdentityDoesNotExistException(identityName);
        }

        // Remove the identity file
        String identityFileName = base64Encode(identityName);
        File identityFile = new File(identitiesFolder, identityFileName);
        // We continue as normal if the identity file does not exist
        if (identityFile.exists()) {
            boolean deleteSucceeded = identityFile.delete();
            if (!deleteSucceeded) {
                throw new IdentityCouldNotBeDeletedException(identityName);
            }
        }

        // Remove the identity from the runtime list
        this.mIdentities.remove(identityName);

        // If the identity is the currently selected identity then we need to deselect it
        if (identityName.equals(this.getCurrentIdentityName())) {
            this.setCurrentIdentity(null);
        }
    }

    /**
     * Base64url decodes a filename.
     *
     * @param file  The file whose filename should be decoded.
     * @throws IllegalArgumentException  If the filename is not valid base64url.
     */
    private String base64DecodeFilename(File file) throws IllegalArgumentException {
        byte[] decodedFileName = Base64.decode(file.getName(), Base64.URL_SAFE);
        String identityName = new String(decodedFileName, Charset.forName("UTF-8"));
        return identityName;
    }

    /**
     * Informs the caller whether this instance is managing any identities.
     *
     * @return True if at least one identity is associated with this manager, false otherwise.
     */
    public boolean containsIdentities() {
        return !this.mIdentities.isEmpty();
    }

    /**
     * Sets the identity that should be used when generating SQRLIdentity objects.
     *
     * @param identityName  The name of the identity to use. If this is null, then all identities are deselected.
     * @throws IdentityDoesNotExistException  If the name of the identity does not correspond to an identity managed by this object.
     */
    public void setCurrentIdentity(String identityName) throws IdentityDoesNotExistException {
        if (identityName != null && !this.mIdentities.containsKey(identityName)) {
            throw new IdentityDoesNotExistException(identityName);
        }

        // At this point, idenityName is either a valid identity name or null
        this.mCurrentIdentity = identityName;
    }

    /**
     * Gets the name of the currently selected identity.
     * 
     * @return The name ofthe current identity.
     */
    public String getCurrentIdentityName() {
        return this.mCurrentIdentity;
    }

    /**
     * Gets a SQRLIdentity instance of the currently selected identity for the given site.
     *
     * @param uri  The SQRLUri for the site.
     */
    public SQRLIdentity getCurrentIdentityForSite(SQRLUri uri) {
        byte[] masterKeyForCurrentIdentity = this.mIdentities.get(this.getCurrentIdentityName());
        if (masterKeyForCurrentIdentity == null) {
            Log.wtf(TAG, "getCurrentIdentityName() returned a string not present in mIdentities");
            throw new RuntimeException();
        }

        // Create the new identity
        SQRLIdentity identity;
        try {
            identity = new SQRLIdentity(masterKeyForCurrentIdentity, uri);
        } catch (InvalidMasterKeyException ex) {
            Log.wtf(TAG, "According to SQRLIdentity, the master key was invalid.", ex);
            throw new RuntimeException(ex);
        } catch (CryptographyException ex) {
            Log.wtf(TAG, "Some kind of cryptography exception occurred when creating a new identity.", ex);
            throw new RuntimeException(ex);
        }

        return identity;
    }
}
