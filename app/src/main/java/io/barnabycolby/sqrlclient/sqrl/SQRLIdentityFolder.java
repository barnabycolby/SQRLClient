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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

/**
 * Provides functionality for interacting with SQRL Identities stored on disk, including loading and saving of identities.
 */
public class SQRLIdentityFolder {
    private File mIdentitiesFolder;

    /**
     * Loads the SQRL identities from disk into a map.
     *
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be loaded.
     */
    public SimpleArrayMap<String, EncryptedIdentity> load() throws IdentitiesCouldNotBeLoadedException {
        SimpleArrayMap<String, EncryptedIdentity> identities = new SimpleArrayMap<String, EncryptedIdentity>();

        // Open the identities folder
        File identitiesFolder = this.getIdentitiesFolder();

        // Loop over the identities, adding them if valid
        for (File identityFolder : identitiesFolder.listFiles()) {
            // Check the folder actually is a folder
            if (!identityFolder.exists() || !identityFolder.isDirectory()) {
                continue;
            }

            // Convert the filename to a human readable string (it should be in base64url format)
            String identityName;
            try {
                identityName = base64DecodeFilename(identityFolder);
            } catch (IllegalArgumentException ex) {
                // Bad base64
                continue;
            }

            // Retrieve the master key and check it is the correct length
            byte[] masterKey = getFileFromDirectoryAsByteArray(identityFolder, "masterKey");
            if (masterKey == null) {
                continue;
            }
            // Master key is 32 bytes, but after encryption this increases to 48
            if (masterKey.length != 48) {
                continue;
            }
            
            // Retrieve the salt
            byte[] salt = getFileFromDirectoryAsByteArray(identityFolder, "salt");
            if (salt == null) {
                continue;
            }

            // Retrieve the iterations and check that it is an integer
            byte[] iterationsAsByteArray = getFileFromDirectoryAsByteArray(identityFolder, "iterations");
            if (iterationsAsByteArray == null) {
                continue;
            }
            int iterations;
            try {
                iterations = ByteBuffer.wrap(iterationsAsByteArray).getInt();
            } catch (BufferUnderflowException ex) {
                continue;
            }

            // Retrieve the iv and check that it is the correct length
            byte[] iv = getFileFromDirectoryAsByteArray(identityFolder, "iv");
            if (iv == null || iv.length != 12) {
                continue;
            }

            // Create the identity and add it to the map
            EncryptedIdentity identity = new EncryptedIdentity(masterKey, salt, iterations, iv);
            identities.put(identityName, identity);
        }

        return identities;
    }
    
    /**
     * Gets the contents of a file from a given directory as a byte array.
     *
     * @param directory  The directory that contains the file.
     * @param filename  The name of the file to read.
     *
     * @return The contents of the file as a byte array, or null if the file does not exist, is a directory, or could not be read.
     */
    private byte[] getFileFromDirectoryAsByteArray(File directory, String filename) {
        // Check that the directory is a directory
        if (!directory.isDirectory()) {
            return null;
        }
        
        // Open the file
        File file = new File(directory, filename);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        // Read the file contents
        byte[] fileContents;
        try {
            fileContents = FileUtils.readFileToByteArray(file);
        } catch (IOException ex) {
            return null;
        }

        return fileContents;
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
     * @param identity  The identity to write.
     * @throws IdentityAlreadyExistsException  If an identity with the same name already exists.
     * @throws IOException  If an IO error occurred.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be opened.
     */
    public void createNewIdentity(String identityName, EncryptedIdentity identity) throws IdentityAlreadyExistsException, IdentitiesCouldNotBeLoadedException, IOException {
        // Create the new folder
        String encodedFilename = base64Encode(identityName);
        File identitiesFolder = this.getIdentitiesFolder();
        File newIdentityFolder = new File(identitiesFolder, encodedFilename);
        if (newIdentityFolder.exists()) {
            throw new IdentityAlreadyExistsException();
        }
        newIdentityFolder.mkdir();

        // Write the components to files
        writeByteArrayToFileInDirectory(newIdentityFolder, "masterKey", identity.getEncryptedMasterKey());
        writeByteArrayToFileInDirectory(newIdentityFolder, "salt", identity.getSalt());
        byte[] iterationsAsByteArray = ByteBuffer.allocate(4).putInt(identity.getIterations()).array(); // Converts the number of iterations to a byte array
        writeByteArrayToFileInDirectory(newIdentityFolder, "iterations", iterationsAsByteArray);
        writeByteArrayToFileInDirectory(newIdentityFolder, "iv", identity.getIv());
    }

    /**
     * Writes a byte array to a new file in a given directory.
     *
     * @param directory  The directory to create the file in.
     * @param filename  The name of the file.
     * @param contents  The byte array to write.
     *
     * @throws IdentityAlreadyExistsException  If the file cannot be created because it already exists.
     */
    private void writeByteArrayToFileInDirectory(File directory, String filename, byte[] contents) throws IdentityAlreadyExistsException, IOException {
        // Create the file, checking whether it already exists
        File file = new File(directory, filename);
        if (file.exists()) {
            throw new IdentityAlreadyExistsException();
        }
        file.createNewFile();

        // Write the byte array to the file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(contents);
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
     *
     * @throws IdentityDoesNotExistException  If the identity given by the identity name does not exist.
     * @throws IdentityCouldNotBeDeletedException  If the identity file on disk could not be deleted.
     * @throws IdentitiesCouldNotBeLoadedException  If the identities folder could not be loaded.
     */
    public void remove(String identityName) throws IdentityDoesNotExistException, IdentityCouldNotBeDeletedException, IdentitiesCouldNotBeLoadedException {
        // Remove the identity file
        String identityFileName = base64Encode(identityName);
        File identityFolder = new File(this.getIdentitiesFolder(), identityFileName);
        // We continue as normal if the identity file does not exist
        if (identityFolder.exists()) {
            boolean deleteSucceeded = deleteFolder(identityFolder);
            if (!deleteSucceeded) {
                throw new IdentityCouldNotBeDeletedException(identityName);
            }
        }
    }

    /**
     * Deletes an entire folder, as Java insists that you delete the inner files first.
     *
     * @param folder  The folder to delete.
     * @return  True if the folder was successfully deleted, false otherwise.
     */
    private boolean deleteFolder(File folder) {
        // Delete the inner files
        for (File file : folder.listFiles()) {
            boolean deleteSucceeded = false;
            if (file.isDirectory()) {
                deleteSucceeded = deleteFolder(file);
            } else {
                deleteSucceeded = file.delete();
            }

            // If the file could not be deleted then we return false
            if (!deleteSucceeded) {
                return false;
            }
        }

        // Delete the actual folder
        return folder.delete();
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
