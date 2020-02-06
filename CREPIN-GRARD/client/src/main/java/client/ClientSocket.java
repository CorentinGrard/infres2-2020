package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientProcessor implements Runnable {

  private Socket sock;
  private ServeurDB db;
  private PrintWriter writer = null;
  private BufferedReader reader = null;
  private BufferedReader clavier = null;
  private ChatChat chat;

  public ClientProcessor(Socket pSock, ServeurDB db) {
    this.sock = pSock;
    this.db = db;
    this.clavier = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Entrez votre username : ");
    try {
      String user = clavier.readLine();
      System.out.println("Entrez votre password : ");
      String password = clavier.readLine();
      this.chat = new ChatChat(user, password);
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public void run() {

    try {
      this.writer = new PrintWriter(sock.getOutputStream(), true);
      this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    } catch (Exception e) {
      e.printStackTrace();
    }

    while (!sock.isClosed()) {
      try {
        sendMessage();
        readMessage();
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
}