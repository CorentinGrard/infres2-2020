package client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ChaCha20_Encryption
{

    public static SecretKey GenerateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
        SecretKey key = new SecretKeySpec(hashedPassword, "ChaCha20");

        return key;
    }

    public static byte[] encrypt(byte[] plaintext, SecretKey key) throws Exception
    {
        byte[] nonceBytes = new byte[12];
        int counter = 5;

        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("ChaCha20");

        // Create ChaCha20ParameterSpec
        ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonceBytes, counter);

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "ChaCha20");

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);

        // Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);

        return cipherText;
    }

    public static String decrypt(byte[] cipherText, SecretKey key) throws Exception
    {
        byte[] nonceBytes = new byte[12];
        int counter = 5;

        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("ChaCha20");

        // Create ChaCha20ParameterSpec
        ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonceBytes, counter);

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "ChaCha20");

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);

        // Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);

        return new String(decryptedText);
    }
}
