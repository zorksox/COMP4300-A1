import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server 
{
    private static LinkedList<chatroom> chatrooms;
    private static LinkedList<user> users;
    private static int port;

    public static void main(String[] args)
    {
        assert (args.length > 0) : "You must specify a port number";
        port = parsePort(args[0]);
        ServerSocket serverSocket = createServerSocket();
        chatrooms = new LinkedList<chatroom>();
        users = new LinkedList<user>();
        acceptConnections(serverSocket);
    }

    static void acceptConnections(ServerSocket serverSocket)
    {
        System.out.println("Server started");

        while (true)
        {
            Socket clientSocket = waitForClientConnection(serverSocket);

            Thread thread = new Thread() 
            {
                public void run()
                {
                    PrintWriter clientOutputStream = getClientPrintWriter(clientSocket);
                    BufferedReader clientInputStream = getClientBufferedReader(clientSocket);
                    user newUser = new user(clientOutputStream);
                    users.add(newUser);
                    System.out.println("Client " + newUser.getId() + " connected");
                    newUser.sendMessage("Connected to server. Welcome User " + newUser.getId() + ".");
                    processTransmissions(clientOutputStream, clientInputStream, serverSocket, newUser);
                }
            };

            thread.start();
        }
    }

    static void processTransmissions(PrintWriter clientOutputStream, BufferedReader clientInputStream, ServerSocket serverSocket, user user)
    {
        String fromClient;
        
        try 
        {
            while ((fromClient = clientInputStream.readLine()) != null) 
            {
                if (user.isInLobby())
                {
                    String response = parseKeywords(fromClient, serverSocket, clientOutputStream, user);
                    user.sendMessage(response);
                }
                else
                {
                    if (fromClient.equals("LEAVE")) leaveChatroom(user);
                    else user.getCurrentRoom().postMessage(fromClient, user);
                }
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

    static ServerSocket createServerSocket()
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

    static String parseKeywords(String clientMessage, ServerSocket serverSocket, PrintWriter clientOutputStream, user user)
    {
        String result = "Command not found";
        String[] tokens = clientMessage.split(" ");
        
        switch (tokens[0])
        {
            case ("EXIT") : { exit(serverSocket); break; }
            case ("CREATE") : { result = createChatroom(tokens, user); break; }
            case ("JOIN") : { result = joinChatroom(tokens, user); break; }
            case ("LIST") : { result = getRoomList(); break; }
            case ("TEST") : {
                result = "Running tests..."; 
                tests.runTests(port, clientOutputStream);
                break; 
            }
        }

        return result;
    }

    static void exit(ServerSocket serverSocket)
    {
        System.out.println("Quiting...");
        try { serverSocket.close(); } 
        catch (Exception e) { System.exit(0);}
        System.exit(1);
    }

    static String createChatroom(String[] roomDetails, user user)
    {
        String result;

        try 
        {
            String name = roomDetails[1];
            int capacity = Integer.parseInt(roomDetails[2]);
            
            for (chatroom c : chatrooms)
            {
                if (c.getName().equals(name))
                    return "A chatroom with that name already exists.";
            }

            chatroom newRoom = new chatroom(name, capacity);
            chatrooms.add(newRoom);
            result = "Room created: " + name;
            System.out.println("User " + user.getId() + " create chatroom " + newRoom.getName() + " with a capacity of " + newRoom.getCapacity());
        }
        catch (Exception e)
        {
            result = "Missing or incorrect parameters. Usage: CREATE [room name] [capacity]";
        }

        return result;
    }

    static String joinChatroom(String[] tokens, user user)
    {
        String result;

        try 
        {
            chatroom room = getRoom(tokens[1]);
            if (room == null) return "Chatroom " + tokens[1] + " does not exist.";
            if (room.getCurrentUserCount() >= room.getCapacity()) return "Room is full.";

            room.addUser(user);
            user.setCurrentRoom(room);

            result = "Joined " + room.getName();
        }
        catch (Exception e)
        {
            result = "Missing or incorrect parameters. Usage: JOIN [room name]";
        }

        return result;
    }

    static String getRoomList()
    {
        String result = "";

        for (chatroom c : chatrooms)
            result += "\n\t" + c.getName() + "\t\tCapacity: " + c.getCurrentUserCount() + "/" + c.getCapacity();

        if (result.equals("")) result = "No rooms exist";

        return result;
    }

    static chatroom getRoom(String name)
    {
        for (chatroom c : chatrooms)
        {
            if (c.getName().equals(name))
                return c;
        }

        return null;
    }

    static void leaveChatroom(user user)
    {
        user.getCurrentRoom().removeUser(user);
        user.setCurrentRoom(null);
    }
}
