import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server 
{
    private LinkedList<chatroom> chatrooms;

    public static void main(String[] args)
    {
        assert (args.length > 0) : "You must specify a port number";
        int port = parsePort(args[0]);
        ServerSocket serverSocket = createServerSocket(port);
        acceptConnections(serverSocket);
    }

    static void acceptConnections(ServerSocket serverSocket)
    {
        while (true)
        {
            Socket clientSocket = waitForClientConnection(serverSocket);
            PrintWriter clientOutputStream = getClientPrintWriter(clientSocket);
            BufferedReader clientInputStream = getClientBufferedReader(clientSocket);
            clientOutputStream.println("Connected to server");
            processTransmissions(clientOutputStream, clientInputStream);
        }
    }

    static void processTransmissions(PrintWriter clientOutputStream, BufferedReader clientInputStream)
    {
        String fromClient;
        
        try 
        {
            while ((fromClient = clientInputStream.readLine()) != null) 
            {
                if (fromClient.equals("EXIT"))
                {
                    clientOutputStream.println("EXIT");
                    System.exit(0);
                }
                clientOutputStream.println(fromClient);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }
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

    static ServerSocket createServerSocket(int port)
    {
        ServerSocket result = null;

        try
        {
            result = new ServerSocket(port);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }

        return result;
    }

    static Socket waitForClientConnection(ServerSocket server)
    {
        Socket result = null;

        try
        {
            result = server.accept();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }

        return result;
    }

    static PrintWriter getClientPrintWriter(Socket clientSocket)
    {
        PrintWriter result = null;

        try
        {
            result = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }

        return result;
    }

    static BufferedReader getClientBufferedReader(Socket clientSocket)
    {
        BufferedReader result = null;

        try
        {
            result = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }

        return result;
    }
}
