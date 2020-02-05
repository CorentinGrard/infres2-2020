package client.infres;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ChaCha20_Encryption
{

    public static SecretKey GenerateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("ChaCha20");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        return key;
    }

    public static byte[] encrypt(String text, SecretKey key) throws Exception
    {
        byte[] plaintext = text.getBytes();
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
