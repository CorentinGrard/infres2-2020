package client;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class ChallengeAccepted {

    String challenge;
    String challengeHashed;
    String hashedPassword;

    public ChallengeAccepted(String challengeRecu,String hashedPasswordRecu) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.challenge = challengeRecu;
        this.hashedPassword = hashedPasswordRecu;
        this.challengeHashed = GenerateHash(challengeRecu,hashedPasswordRecu);
    }

    public static String GenerateHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hashedPassword);
    }

}
