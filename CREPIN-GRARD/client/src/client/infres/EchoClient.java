package client.infres;


import javax.crypto.SecretKey;

import java.util.Base64;

import static client.infres.ChaCha20_Encryption.*;

public class EchoClient {

    public static void main(String[] args) throws Exception
    {
        // CHACHA
        SecretKey key = GenerateKey();
        System.out.println("Original Key  : " + key);
        String plainText = "Hello";

        System.out.println("Original Text  : " + plainText);

        byte[] cipherText = encrypt(plainText, key);
        System.out.println("Encrypted Text : " + Base64.getEncoder().encodeToString(cipherText));

        String decryptedText = decrypt(cipherText, key);
        System.out.println("DeCrypted Text : " + decryptedText);

        // SOCKET
        ClientSocket client2 = new ClientSocket();
        client2.startConnection("127.0.0.1", 5555);

        String msg1 = client2.sendMessage(Base64.getEncoder().encodeToString(cipherText));
        client2.stopConnection();
    }

}