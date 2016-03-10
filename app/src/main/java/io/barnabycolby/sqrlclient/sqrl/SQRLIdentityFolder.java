package io.barnabycolby.sqrlclient.sqrl;

import android.support.v4.util.SimpleArrayMap;
import android.util.Base64;

import io.barnabycolby.sqrlclient.App;
import io.barnabycolby.sqrlclient.exceptions.IdentitiesCouldNotBeLoadedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityAlreadyExistsException;
import io.barnabycolby.sqrlclient.exceptions.IdentityCouldNotBeDeletedException;
import io.barnabycolby.sqrlclient.exceptions.IdentityDoesNotExistException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

/**
 * Provides functionality for interacting with SQRL Identities stored on disk, including loading and saving of identities.
 */
public class SQRLIdentityFolder {
    private File mIdentitiesFolder;

    /**
     * Loads the SQRL identities from disk into a map.
     */
    public SimpleArrayMap<String, byte[]> load() throws IdentitiesCouldNotBeLoadedException {
        SimpleArrayMap<String, byte[]> identities = new SimpleArrayMap<String, byte[]>();

        // Open the identities folder
        File identitiesFolder = this.getIdentitiesFolder();

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
            identities.put(identityName, masterKey);
        }

        return identities;
    }

    /**
     * Returns a handle to the identities folder.
     */
    private File getIdentitiesFolder() throws IdentitiesCouldNotBeLoadedException {
        if (this.mIdentitiesFolder == null || !this.mIdentitiesFolder.exists()) {
            this.mIdentitiesFolder = openIdentitiesFolder();
        }

        return this.mIdentitiesFolder;
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
     * Writes a new identity to the internal file storage.
     *
     * @param identityName  The name of the new identity. This will be used for UI identification and system identification.
     * @param masterKey  The master key of the new identity.
     * @throws IdentityAlreadyExistsException  If an identity with the same name already exists.
     * @throws IOException  If an IO error occurred.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be opened.
     */
    public void createNewIdentity(String identityName, byte[] masterKey) throws IdentityAlreadyExistsException, IdentitiesCouldNotBeLoadedException, IOException {
        // Create the new file
        String encodedFilename = base64Encode(identityName);
        File identitiesFolder = this.getIdentitiesFolder();
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
     * Removes a single identity from the list.
     *
     * @param identityName  The name of the identity to remove.
     * @throws IdentityDoesNotExistException  If the identity given by the identity name does not exist.
     * @throws IdentityCouldNotBeDeletedException  If the identity file on disk could not be deleted.
     */
    public void remove(String identityName) throws IdentityDoesNotExistException, IdentityCouldNotBeDeletedException, IdentitiesCouldNotBeLoadedException {
        // Remove the identity file
        String identityFileName = base64Encode(identityName);
        File identityFile = new File(this.getIdentitiesFolder(), identityFileName);
        // We continue as normal if the identity file does not exist
        if (identityFile.exists()) {
            boolean deleteSucceeded = identityFile.delete();
            if (!deleteSucceeded) {
                throw new IdentityCouldNotBeDeletedException(identityName);
            }
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
}
