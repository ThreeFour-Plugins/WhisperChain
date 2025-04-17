package me.threefour.whisperchain.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Encryption {
    
    private final Map<UUID, SecretKey> playerKeys = new HashMap<>();
    
    /**
     * Generate a new encryption key for a player
     * @param playerUUID The UUID of the player
     * @return The generated secret key
     */
    public SecretKey generateKeyForPlayer(UUID playerUUID) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            playerKeys.put(playerUUID, secretKey);
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a player's encryption key
     * @param playerUUID The UUID of the player
     * @return The player's secret key, or generates a new one if none exists
     */
    public SecretKey getPlayerKey(UUID playerUUID) {
        if (!playerKeys.containsKey(playerUUID)) {
            return generateKeyForPlayer(playerUUID);
        }
        return playerKeys.get(playerUUID);
    }
    
    /**
     * Encrypt a message using AES encryption
     * @param message The message to encrypt
     * @param key The key to use for encryption
     * @return The encrypted message as a Base64 string
     */
    public String encrypt(String message, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Decrypt a message using AES encryption
     * @param encryptedMessage The encrypted message as a Base64 string
     * @param key The key to use for decryption
     * @return The decrypted message
     */
    public String decrypt(String encryptedMessage, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Convert a secret key to a string for storage
     * @param secretKey The secret key to convert
     * @return The secret key as a Base64 string
     */
    public String keyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    /**
     * Convert a string to a secret key
     * @param keyString The Base64 encoded key string
     * @return The secret key
     */
    public SecretKey stringToKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
} 