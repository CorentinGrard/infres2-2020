package serveur;

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

  public ClientProcessor(Socket pSock, ServeurDB db) {
    this.sock = pSock;
    this.db = db;
    this.clavier = new BufferedReader(new InputStreamReader(System.in));
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
    this.db.addMessage("Serveur", str);
    this.writer.println(str);
    if (str == "END") {
      stopConnection();
    }
  }

  public void readMessage() throws IOException {
    String resp = reader.readLine();
    this.db.addMessage("Client", resp);
    System.out.println("Client : " + resp);
    if (resp == "END") {
      stopConnection();
    }
  }

  public void stopConnection() throws IOException {
    reader.close();
    writer.close();
    sock.close();
    clavier.close();
  }
}