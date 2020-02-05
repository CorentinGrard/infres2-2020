package client.infres;

import java.net.*;
import java.io.*;

public class ClientSocket {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public void startConnection(String name, int port) throws IOException {
        clientSocket = new Socket(name, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        out.println("END") ;
        in.close();
        out.close();
        clientSocket.close();
    }

}
