package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class ClientProcessor implements Runnable {

  private Socket sock;
  private ServeurDB db;
  private PrintWriter writer = null;
  private BufferedReader reader = null;
  private BufferedReader clavier = null;
  private ChatChat chat;
  private String user;
  private String password;
  private String secret;

  public ClientProcessor(Socket pSock, ServeurDB db) {
    this.sock = pSock;
    this.db = db;
    this.clavier = new BufferedReader(new InputStreamReader(System.in));
    try {
      System.out.println("Entrez votre username : ");
      this.user = clavier.readLine();
      System.out.println("Entrez votre password : ");
      this.password = clavier.readLine();
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public void run() {
    try {
      this.writer = new PrintWriter(sock.getOutputStream(), true);
      this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

      // DH

      String to_decode = reader.readLine();
      byte[] alicePubKeyEnc = Base64.getDecoder().decode(to_decode);

      KeyFactory serveurKeyFac = KeyFactory.getInstance("DH");
      X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);

      PublicKey alicePubKey = serveurKeyFac.generatePublic(x509KeySpec);

      /*
       * Serveur gets the DH parameters associated with Alice's public key. He must use
       * the same parameters when he generates his own key pair.
       */
      DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey) alicePubKey).getParams();

      // Serveur creates his own DH key pair
      System.out.println("Serveur: Generate DH keypair ...");
      KeyPairGenerator serveurKpairGen = KeyPairGenerator.getInstance("DH");
      serveurKpairGen.initialize(dhParamFromAlicePubKey);
      KeyPair serveurKpair = serveurKpairGen.generateKeyPair();

      // Serveur creates and initializes his DH KeyAgreement object
      System.out.println("Serveur: Initialization ...");
      KeyAgreement serveurKeyAgree = KeyAgreement.getInstance("DH");
      serveurKeyAgree.init(serveurKpair.getPrivate());

      // Serveur encodes his public key, and sends it over to Alice.
      byte[] serveurPubKeyEnc = serveurKpair.getPublic().getEncoded();
      String to_send = Base64.getEncoder().encodeToString(serveurPubKeyEnc);
      writer.println(to_send);

      System.out.println("Serveur: Execute PHASE1 ...");
      serveurKeyAgree.doPhase(alicePubKey, true);

      byte[] serveurSharedSecret = new byte[256];

      serveurKeyAgree.generateSecret(serveurSharedSecret, 0);
      this.secret = toHexString(serveurSharedSecret);
      System.out.println("Serveur secret: " + toHexString(serveurSharedSecret));

      // Challenge
      System.out.println("Challenge Sent");
      Challenge challenge = new Challenge(this.user);
      writer.println(challenge.getChallenge());
      String hashChallenge = reader.readLine();
      if (!challenge.compareChallenge(hashChallenge)) {
        stopConnection();
      }
      writer.println("Challenge Completed");
      System.out.println("Challenge Completed");

      // Encryption
      this.chat = new ChatChat(this.user, this.secret);
    } catch (Exception e) {
      e.printStackTrace();
    }

    while (!sock.isClosed()) {
      try {
        readMessage();
        sendMessage();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendMessage() throws IOException {
    System.out.println("Enter your message : ");
    String str = this.clavier.readLine();
    try {
      String encrypt = chat.encrypt(str);
      this.db.addMessage("Client", encrypt);
      writer.println(encrypt);
      if (str == "END") {
        stopConnection();
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public void readMessage() throws IOException {
    String crypted = reader.readLine();
    this.db.addMessage("Serveur", crypted);
    try {
      String resp = chat.decrypt(crypted);
      System.out.println("Serveur : " + resp);
      if (resp == "END") {
        stopConnection();
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public void stopConnection() throws IOException {
    reader.close();
    writer.close();
    sock.close();
    clavier.close();
  }

      /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer buf) {
      char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
              '9', 'A', 'B', 'C', 'D', 'E', 'F' };
      int high = ((b & 0xf0) >> 4);
      int low = (b & 0x0f);
      buf.append(hexChars[high]);
      buf.append(hexChars[low]);
  }

  /*
   * Converts a byte array to hex string
   */
  private static String toHexString(byte[] block) {
      StringBuffer buf = new StringBuffer();
      int len = block.length;
      for (int i = 0; i < len; i++) {
          byte2hex(block[i], buf);
          if (i < len-1) {
              buf.append(":");
          }
      }
      return buf.toString();
  }
}