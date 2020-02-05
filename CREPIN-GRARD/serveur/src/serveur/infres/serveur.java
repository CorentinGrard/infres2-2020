import java.net.*;
import java.io.*;

public class serveur {
    final static int port = 1030;
    public static void main(String[] args) {
        try {
            ServerSocket socketServeur = new ServerSocket(port);
            System.out.println("Le serveur est à l'écoute...");
            while (true) {
                Socket socketClient = socketServeur.accept();
                String message = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                PrintStream out = new PrintStream(socketClient.getOutputStream());
                message = in.readLine();
                System.out.println("Connexion établie avec " + socketClient.getInetAddress());
                System.out.println("Chaîne reçue : " + message + "\n");
                out.println(message);
                socketClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}