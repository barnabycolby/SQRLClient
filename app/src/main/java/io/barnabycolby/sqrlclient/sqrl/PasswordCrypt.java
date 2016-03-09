package io.barnabycolby.sqrlclient.sqrl;

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
public class PasswordCrypt {
    private byte[] mSalt;
    private byte[] mIv;
    private int mIterations;

    // The tag value was randomly generated
    private byte[] mTag = "wOGRGI$H6AAaxX77GZ\"(aetC]ChZBfz_B:ef_HjPtWua%\"&zaze]0iK(<[y%{Zd".getBytes();

    /**
     * This constructor should be used when you want to encrypt the master key.
     */
    public PasswordCrypt() {}

    /**
     * This constructor should be used when you want to decrypt the master key.
     */
    public PasswordCrypt(byte[] salt, int iterations, byte[] iv) {
        this.mSalt = salt;
        this.mIterations = iterations;
        this.mIv = iv;
    }
    
    /**
     * Encrypts a master key using a password.
     *
     * @param masterKey  The master key to encrypt.
     * @param password  The password to encrypt the master key with.
     * @throws GeneralSecurityException  If the encryption cannot be completed.
     */
    public byte[] encryptMasterKey(byte[] masterKey, String password) throws GeneralSecurityException {
        // Generate the encryption key
        EnScrypt enScrypt = new EnScrypt();
        byte[] derivedKey = enScrypt.deriveKeyFor5Seconds(password, this.generateSalt());
        this.mIterations = enScrypt.getIterations();
        Key key = new SecretKeySpec(derivedKey, "AES");

        // Perform the encryption
        GCMParameterSpec params = new GCMParameterSpec(128, this.generateIv());
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, params);
        cipher.updateAAD(this.mTag);
        return cipher.doFinal(masterKey);
    }

    /**
     * Decrypts a master key using the given password.
     *
     * Requires additional non-secret parameters that should be obtained from this class after calling encryptMasterKey.
     *
     * @param encryptedMasterKey  The master key to decrypt.
     * @param password  The password used to encrypt the master key.
     * @param iterations  The number of EnScrypt iterations performed when deriving the encryption key from the password.
     * @param iv  The IV value that was used for the encryption.
     *
     * @throws AEADBadTagException  If the password used was incorrect.
     * @throws GeneralSecurityException  If the decryption could not be completed.
     */
    public byte[] decryptMasterKey(byte[] encryptedMasterKey, String password) throws GeneralSecurityException {
        // Generate the decryption key
        EnScrypt enScrypt = new EnScrypt();
        byte[] derivedKey = enScrypt.deriveKey(password, this.getSalt(), this.getIterations());
        Key key = new SecretKeySpec(derivedKey, "AES");

        // Perform the decryption
        GCMParameterSpec params = new GCMParameterSpec(128, this.getIv());
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, params);
        cipher.updateAAD(this.mTag);
        return cipher.doFinal(encryptedMasterKey);
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

    private byte[] generateSalt() {
        final Random rng = new SecureRandom();
        this.mSalt = new byte[16];
        rng.nextBytes(this.mSalt);
        return this.mSalt;
    }

    private byte[] generateIv() {
        SecureRandom rng = new SecureRandom();
        this.mIv = new byte[12];
        rng.nextBytes(this.mIv);
        return this.mIv;
    }
}
