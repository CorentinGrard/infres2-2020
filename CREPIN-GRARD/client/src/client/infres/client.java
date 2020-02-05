package client.infres;

import java.net.*;
import java.io.*;

public class client {
  final static int port = 1030;

  public static void main(String[] args) {
    Socket socket;
    DataInputStream userInput;
    PrintStream theOutputStream;
    try {
      InetAddress serveur = InetAddress.getByName("localhost");
      socket = new Socket(serveur, port);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintStream out = new PrintStream(socket.getOutputStream());
      out.println(args[0]);
      System.out.println(in.readLine());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
