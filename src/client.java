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
        String fromServer;
        String toServer;

        try
        {
            while ((fromServer = serverInputStream.readLine()) != null) 
            {
                if (fromServer.equals("EXIT")) exit(userInput, serverSocket);

                System.out.println("Server: " + fromServer);
                toServer = userInput.nextLine();

                if (toServer != null) 
                    serverOutputStream.println(toServer);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }
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
