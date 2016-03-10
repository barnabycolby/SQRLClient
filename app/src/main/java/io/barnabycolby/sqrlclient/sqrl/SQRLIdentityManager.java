package io.barnabycolby.sqrlclient.sqrl;

import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import io.barnabycolby.sqrlclient.exceptions.CryptographyException;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeDeletedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeWrittenToDiskException;
import io.barnabycolby.sqrlclient.exceptions.IdentityDoesNotExistException;
import io.barnabycolby.sqrlclient.exceptions.InvalidMasterKeyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private SQRLIdentityFolder mIdentityFolder;

    /**
     * Constructs a new instance of the SQRLIdentityManager.
     *
     * Handles the loading of identities from disk.
     * 
     * @throws IdentitiesCouldNotBeLoadedException  If the identities could not be loaded from disk.
     */
    public SQRLIdentityManager() throws IdentitiesCouldNotBeLoadedException {
        this.mIdentityFolder = new SQRLIdentityFolder();
        this.mIdentities = this.mIdentityFolder.load();
    }

    /**
     * Saves a new identity to the system.
     *
     * @param identityName  The name of the new identity. This will be used for UI identification and system identification.
     * @param masterKey  The master key of the new identity.
     * @param password  The password that protects the new identity.
     * @throws IdentityAlreadyExistsException  If an identity with the same name already exists.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be opened.
     */
    public void save(String identityName, byte[] masterKey, String password) throws IdentityAlreadyExistsException, IdentityCouldNotBeWrittenToDiskException, IdentitiesCouldNotBeLoadedException {
        if (mIdentities.containsKey(identityName)) {
            throw new IdentityAlreadyExistsException();
        }

        // We write it to disk before adding it to the runtime array in case the writeNewIdentityToDisk call throws an exception
        try {
            this.mIdentityFolder.createNewIdentity(identityName, masterKey);
        } catch (IOException ex) {
            Log.e(TAG, "Could not write new identity to disk: " + ex.getMessage());
            throw new IdentityCouldNotBeWrittenToDiskException();
        }
        mIdentities.put(identityName, masterKey);
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
        // We need to iterate in reverse order as the removeIdentity call will remove the item from the list
        for (int i = this.mIdentities.size() - 1; i >= 0; i--) {
            String identityName = this.mIdentities.keyAt(i);
            try {
                this.removeIdentity(identityName);
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
     * @throws IdentityDoesNotExistException  If the identity given by the identity name does not exist.
     * @throws IdentityCouldNotBeDeletedException  If the identity file on disk could not be deleted.
     */
    public void removeIdentity(String identityName) throws IdentityDoesNotExistException, IdentityCouldNotBeDeletedException, IdentitiesCouldNotBeLoadedException {
        // Check that the given identityName is valid
        if (!this.mIdentities.containsKey(identityName)) {
            throw new IdentityDoesNotExistException(identityName);
        }

        // Remove the identity
        this.mIdentityFolder.remove(identityName);
        this.mIdentities.remove(identityName);

        // If the identity is the currently selected identity then we need to deselect it
        if (identityName.equals(this.getCurrentIdentityName())) {
            this.setCurrentIdentity(null);
        }
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
     * @param password  The password to unlock the identity.
     */
    public SQRLIdentity getCurrentIdentityForSite(SQRLUri uri, String password) {
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

    /**
     * Checks whether an identity exists.
     *
     * @param identityName  The name of the identity to check for.
     * @return  True if the identity exists, false otherwise.
     */
    public boolean identityExists(String identityName) {
        return this.mIdentities.containsKey(identityName);
    }
}
