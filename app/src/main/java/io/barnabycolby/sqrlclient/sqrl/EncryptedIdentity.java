package io.barnabycolby.sqrlclient.sqrl;

import io.barnabycolby.sqrlclient.sqrl.DecryptIdentityListener;

import java.util.Random;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Provides the required functionality for encrypting/decrypting SQRL master keys using passwords, according to the SQRL protocol.
 */
public class EncryptedIdentity {
    private byte[] mEncryptedMasterKey;
    private byte[] mSalt;
    private byte[] mIv;
    private int mIterations;

    // The tag value was randomly generated
    private static byte[] sTag = "wOGRGI$H6AAaxX77GZ\"(aetC]ChZBfz_B:ef_HjPtWua%\"&zaze]0iK(<[y%{Zd".getBytes();

    /**
     * Constructs a new encrypted identity, using the encrypted master key and other information required for decryption.
     *
     * The parameters should be obtained from a previous instance of EncryptedIdentity, instantiated using EncryptedIdentity.generate(...).
     *
     * @param encryptedMasterKey  The identities master key in encrypted form.
     * @param salt  The EnScrypt salt used to derive the encryption key from the password.
     * @param iterations  The number of EnScrypt iterations used to derive the key.
     * @param iv  The IV that was used to encrypt the master key.
     */
    public EncryptedIdentity(byte[] encryptedMasterKey, byte[] salt, int iterations, byte[] iv) {
        this.mEncryptedMasterKey = encryptedMasterKey;
        this.mSalt = salt;
        this.mIterations = iterations;
        this.mIv = iv;
    }
    
    /**
     * Creates an EncryptedIdentity instance by encrypting the given master key with the given password.
     *
     * Callers of this function should be aware that it will take at least 5 seconds to return.
     *
     * @param masterKey  The master key to encrypt.
     * @param password  The password to encrypt the master key with.
     * @throws GeneralSecurityException  If the encryption cannot be completed.
     */
    public static EncryptedIdentity create(byte[] masterKey, String password) throws GeneralSecurityException {
        // Generate the encryption key
        EnScrypt enScrypt = new EnScrypt();
        byte[] salt = EncryptedIdentity.generateSalt();
        byte[] derivedKey = enScrypt.deriveKeyFor5Seconds(password, salt);
        int iterations = enScrypt.getIterations();
        Key key = new SecretKeySpec(derivedKey, "AES");

        // Perform the encryption
        byte[] iv = EncryptedIdentity.generateIv();
        GCMParameterSpec params = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, params);
        cipher.updateAAD(EncryptedIdentity.sTag);
        byte[] encryptedMasterKey = cipher.doFinal(masterKey);

        return new EncryptedIdentity(encryptedMasterKey, salt, iterations, iv);
    }

    /**
     * Decrypts the identities master key using the given password.
     *
     * Callers of this function should be aware that it will take at least 5 seconds to return.
     *
     * @param password  The password used to encrypt the master key.
     * @param listener  The listener for decryption progress updates.
     *
     * @throws AEADBadTagException  If the password used was incorrect.
     * @throws GeneralSecurityException  If the decryption could not be completed.
     */
    public byte[] decrypt(String password, DecryptIdentityListener listener) throws GeneralSecurityException {
        // Generate the decryption key
        EnScrypt enScrypt = new EnScrypt(listener);
        byte[] derivedKey = enScrypt.deriveKey(password, this.getSalt(), this.getIterations());
        Key key = new SecretKeySpec(derivedKey, "AES");

        // Perform the decryption
        GCMParameterSpec params = new GCMParameterSpec(128, this.getIv());
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, params);
        cipher.updateAAD(EncryptedIdentity.sTag);
        return cipher.doFinal(this.mEncryptedMasterKey);
    }

    /**
     * Gets the master key in encrypted form.
     */
    public byte[] getEncryptedMasterKey() {
        return this.mEncryptedMasterKey;
    }

    /**
     * Gets the salt used for EnScrypt key derivation.
     */
    public byte[] getSalt() {
        return this.mSalt;
    }

    /**
     * Gets the IV used for AES-GCM encryption.
     */
    public byte[] getIv() {
        return this.mIv;
    }

    /**
     * Gets the number of EnScrypt iterations performed during the key derivation.
     */
    public int getIterations() {
        return this.mIterations;
    }

    private static byte[] generateSalt() {
        final Random rng = new SecureRandom();
        byte[] salt = new byte[16];
        rng.nextBytes(salt);
        return salt;
    }

    private static byte[] generateIv() {
        SecureRandom rng = new SecureRandom();
        byte[] iv = new byte[12];
        rng.nextBytes(iv);
        return iv;
    }
}
