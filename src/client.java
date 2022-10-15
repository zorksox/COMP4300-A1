import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.*;

public class client 
{
    public static void main(String[] args)
    {
        assert(args.length > 1) : "Incorrect number of arguments. \njava client [server name] [port number]";
        String hostName = args[0];
        int portNumber = parsePort(args[1]);
        startConnection(hostName, portNumber);
    }

    static void startConnection(String hostName, int portNumber)
    {
        Scanner userInput = new Scanner(System.in);
        Socket serverSocket = connectToServer(hostName, portNumber);
        PrintWriter serverOutputStream = getServerPrintWriter(serverSocket);
        BufferedReader serverInputStream = getServerBufferedReader(serverSocket);
        processTransmissions(serverInputStream, serverOutputStream, userInput, serverSocket);
    }

    static int parsePort(String port)
    {
        int result = -1;

        try 
        {
            result = Integer.parseInt(port);
        }
        catch (NumberFormatException e) 
        {
            System.out.println("Parameter must be valid port number.");
            System.exit(0);
        }

        return result;
    }

    static Socket connectToServer(String hostName, int portNumber)
    {
        Socket result = null;

        try
        {
            result = new Socket(hostName, portNumber);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }
        
        return result;
    }

    static PrintWriter getServerPrintWriter(Socket serverSocket)
    {
        PrintWriter result = null;

        try
        {
            result = new PrintWriter(serverSocket.getOutputStream(), true);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }

        return result;
    }

    static BufferedReader getServerBufferedReader(Socket serverSocket)
    {
        BufferedReader result = null;

        try
        {
            result = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }

        return result;
    }

    static void processTransmissions(BufferedReader serverInputStream, PrintWriter serverOutputStream, Scanner userInput, Socket serverSocket)
    {
        Thread serverThread = new Thread() 
        {
            public void run()
            {
                String fromServer;
                
                try 
                {
                    while ((fromServer = serverInputStream.readLine()) != null) 
                    {
                        //trim ending control character. 
                        System.out.println(fromServer.substring(0, fromServer.length()-1));
                    }
                } catch (IOException e) 
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        Thread clientThread = new Thread() 
        {
            public void run()
            {
                String toServer;

                while ((toServer = userInput.nextLine()) != null) 
                {
                    //Send message to server.
                    if (toServer.equals("EXIT")) exit(userInput, serverSocket);
                    else if (toServer != null) serverOutputStream.println(toServer);
                }
            }
        };

        clientThread.start();
        serverThread.start();
    }

    static void exit(Scanner userInput, Socket serverSocket)
    {
        System.out.println("Quiting...");
        userInput.close();
        try { serverSocket.close(); } 
        catch (IOException e) { System.exit(0);}
        System.exit(1);
    }
}
