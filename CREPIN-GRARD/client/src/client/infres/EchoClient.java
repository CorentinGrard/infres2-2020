package client.infres;

public class EchoClient {

    public static void main(String[] args) throws Exception
    {
        ClientSocket client2 = new ClientSocket();
        client2.startConnection("127.0.0.1", 5555);
        String msg1 = client2.sendMessage("hello");
        String msg2 = client2.sendMessage("world");
        client2.stopConnection();
    }

}