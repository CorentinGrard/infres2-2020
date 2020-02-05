package serveur;

import java.net.*;
import java.io.*;

public class serveur {
    final static int port = 1030;
    public static void main(String[] args) {
        try {
            ServerSocket socketServeur = new ServerSocket(port);
            System.out.println("Le serveur est à l'écoute sur le port "+ port);
            while (true) {
                Socket socketClient = socketServeur.accept();
                System.out.println("Connexion établie avec " + socketClient.getInetAddress());
                String message = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                PrintStream out = new PrintStream(socketClient.getOutputStream());
                while(message.equals("Bye")){
                    message = in.readLine();
                    System.out.println(message + "\n");
                    out.println(message);
                }
                socketClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}