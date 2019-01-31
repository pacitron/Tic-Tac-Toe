import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;



public class Server {
    private Hashtable<String, Server.ConnectionThread> connections = new Hashtable();
    private String[][] array = {{" "," "," "},{" "," "," "},{" "," "," "}};

    public Server() {
        ServerSocket serversocket= null;
        try {
            serversocket= new ServerSocket(1234);
        } catch (IOException e) {
            System.err.println("Could not listen on port...");
            System.exit(-1);
        }

        System.out.println("Server listening...");
        int count=0;
        String player="X";

        while (count<=2){
            try {
                Socket s1=serversocket.accept();
                System.out.println("Player"+(count+1)+"joined");
                count++;
                if(count==2){
                    player="O";
                }
                ConnectionThread st1 = new ConnectionThread(s1,player);
                st1.start();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    private class ConnectionThread extends Thread{
        private Socket socket = null;
        private OutputStream outputStream = null;
        private InputStream inputStream = null;

        private String player="";
        private String user="";
        private int whoseTurn=0;
        private boolean gameOver=false;
        private boolean isFull=false;
        private boolean playerWon=false;

        public ConnectionThread(Socket socket,String player) {
            super("ConnectionThread");
            this.socket=socket;
            this.player=player;
        }

        public void run(){
            try {
                outputStream=socket.getOutputStream();
                inputStream = socket.getInputStream();
                BufferedReader inputreader= new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter outputwriter= new PrintWriter(outputStream);

                System.out.println("ListenThread for player " + player);
                    connections.put(player, this);
                outputwriter.println("Waiting for opponent....");
                outputwriter.flush();
                while (connections.get("O")==null){
                    continue;
                }
                outputwriter.println("Game begins...\nYou Are : "+player);
                outputwriter.flush();

                //Tic Tac Toe logic
                while (!gameOver){

                    ConnectionThread d1=connections.get("X");
                    ConnectionThread d2=connections.get("O");

                    //play game
                    if(whoseTurn%2==0){
                        user="X";
                    } else {
                        user="O";
                    }
                    ConnectionThread connection=connections.get(user);
                    connection.yourTurn(user);
                    whoseTurn++;

                    //display board to both players and server
                    d1.displayBoard();
                    d2.displayBoard();
                    System.out.println(array[0][0]+"     |   "+array[0][1]+"    |    "+array[0][2]+"\n"+"----"+"--|--"+"----"+"--|--"+"----"+"\n"+array[1][0]+"     |   "+array[1][1]+"    |    "+array[1][2]+"\n"+"----"+"--|--"+"----"+"--|--"+"----"+"\n"+array[2][0]+"     |   "+array[2][1]+"    |    "+array[2][2]+"\n");


                    //check player win conditions
                    for(int i=0;i<3;i++){
                        if(array[i][0].equals("X")&&array[i][1].equals("X")&&array[i][2].equals("X") || array[0][i].equals("X")&&array[1][i].equals("X")&&array[2][i].equals("X") || array[0][0].equals("X")&&array[1][1].equals("X")&&array[2][2].equals("X") || array[0][2].equals("X")&&array[1][1].equals("X")&&array[2][0].equals("X")){
                            d1.displayXWin();
                            d2.displayXWin();
                            System.out.println("\nPlayer X won\n Closing server...");
                            playerWon=true;
                            break;
                        }
                        if(array[i][0].equals("O")&&array[i][1].equals("O")&&array[i][2].equals("O") || array[0][i].equals("O")&&array[1][i].equals("O")&&array[2][i].equals("O") || array[0][0].equals("O")&&array[1][1].equals("O")&&array[2][2].equals("O") || array[0][2].equals("O")&&array[1][1].equals("O")&&array[2][0].equals("O")){
                            d1.displayOWin();
                            d2.displayOWin();
                            System.out.println("\nPlayer O won\n Closing server...");
                            playerWon=true;
                            break;
                        }
                    }

                    //check if board is full
                    int isFullCount=0;
                    for(int i=0;i<3;i++){
                        for(int j=0;j<3;j++){
                            if(array[i][j].equals(" ")){
                                isFullCount++;
                            }
                        }
                    }
                    if(isFullCount==0){
                        isFull=true;
                        if(!playerWon){
                            d1.displayTie();
                            d2.displayTie();
                            System.out.println("\nGame ended in a tie.\n Closing server...");
                        }
                    }

                    //check game over condition
                    if(isFull || playerWon){
                        gameOver=true;
                    }
                }
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        //get players' option and update the board
        public void yourTurn(String user){
            try {
                BufferedReader inputreader= new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter outputWriter = new PrintWriter(outputStream);
                while (true) {
                    outputWriter.println("Play yor move...");
                    outputWriter.flush();
                    String input = inputreader.readLine();
                    String[] move = input.split(",");
                    if(move.length!=2){
                        outputWriter.println("Invalid move...");
                        outputWriter.flush();
                        continue;
                    }
                    int i= 0;
                    int j= 0;
                    try {
                        i = Integer.parseInt(move[0]);
                        j = Integer.parseInt(move[1]);
                    } catch (NumberFormatException e) {
                        outputWriter.println("Invalid move...");
                        outputWriter.flush();
                        continue;
                    }
                    if(i>2 || j>2 || i<0 || j<0){
                        outputWriter.println("Invalid move...");
                        outputWriter.flush();
                        continue;
                    } else if (array[i][j]!=" "){
                        outputWriter.println("Invalid move...");
                        outputWriter.flush();
                        continue;
                    } else {
                        array[i][j]=user;
                        System.out.println("Player "+user+" played: "+i+","+j);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        //display board to players
        public void displayBoard(){
            PrintWriter outputwriter = new PrintWriter(outputStream);
            outputwriter.println(array[0][0]+"     |   "+array[0][1]+"    |    "+array[0][2]+"\n"+"----"+"--|--"+"----"+"--|--"+"----"+"\n"+array[1][0]+"     |   "+array[1][1]+"    |    "+array[1][2]+"\n"+"----"+"--|--"+"----"+"--|--"+"----"+"\n"+array[2][0]+"     |   "+array[2][1]+"    |    "+array[2][2]+"\n");
            outputwriter.flush();
        }

        //display game as tie
        public void displayTie(){
            PrintWriter outputwriter = new PrintWriter(outputStream);
            outputwriter.println("Game is a tie....");
            outputwriter.flush();
        }

        //display player X win
        public void displayXWin(){
            PrintWriter outputwriter = new PrintWriter(outputStream);
            outputwriter.println("Player X won!");
            outputwriter.flush();
        }

        //display player O win
        public void displayOWin(){
            PrintWriter outputwriter = new PrintWriter(outputStream);
            outputwriter.println("Player O won!");
            outputwriter.flush();
        }
    }
}
