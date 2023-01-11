import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serverSocket;
    Socket client;
    PrintWriter output;
    BufferedReader input;
    int clientCounter;

    HashSet<PlayerHandler> handlers;

    public static void main(String[] args){

    }

    Server(){
    }

    class PlayerHandler extends Thread{

    }
}
