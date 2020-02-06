package main.java.serveur;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Challenge {

    String monChallenge;
    String monChallengeHashed;
    String hashedPassword;

    public Challenge(String hashedPasswordRecu) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] challenge = new byte[16];
        random.nextBytes(challenge);
        this.hashedPassword = hashedPasswordRecu;
        this.monChallenge = Base64.getEncoder().encodeToString(challenge);
        this.monChallengeHashed = GenerateHash(monChallenge,hashedPasswordRecu);
    }

    public static String GenerateHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public boolean compareChallenge(String challengeHashed){
        return(challengeHashed.equals(monChallengeHashed));
    }

}
