import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public Client() {
        try {
            Socket socket = new Socket("127.0.0.1", 1234);
            PrintWriter outputwriter = new PrintWriter(socket.getOutputStream());
            //BufferedReader inputreader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner keyinput = new Scanner(System.in);

            ListenThread lt = new ListenThread(socket);
            lt.start();

            while (true) {
                String command = keyinput.nextLine();
                outputwriter.println(command);
                outputwriter.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }

    private class ListenThread extends Thread {
        Socket socket = null;

        public ListenThread(Socket socket) {
            super("ListenThread");
            this.socket=socket;
        }

        public void run() {
            try {
                PrintWriter outputWriter = new PrintWriter(socket.getOutputStream());
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("RULES:\n*) To play a move, enter:\n   0,0    0,1    0,2\n   1,0    1,1    1,2\n   2,0    2,1    2,2\n*) Player X goes first\n");
                boolean end = false;
                while (!end) {
                    //wait for commands
                    String message = inputReader.readLine();
                    if (message != null)
                        System.out.println(message);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("----GAME OVER----");
                System.exit(1);
            }
        }
    }
}