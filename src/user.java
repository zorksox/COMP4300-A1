import java.io.PrintWriter;

public class user 
{
    private static int maxId = 0;
    private int id;
    private chatroom currentRoom;
    private PrintWriter outputStream;

    public user(PrintWriter outputStream)
    {
        id = maxId++;
        currentRoom = null;
        this.outputStream = outputStream;
    }

    public int getId()
    {
        return id;
    }

    public void setCurrentRoom(chatroom room)
    {
        currentRoom = room;
    }

    public chatroom getCurrentRoom()
    {
        return currentRoom;
    }

    public boolean isInLobby()
    {
        return currentRoom == null;
    }

    public void sendMessage(String message)
    {
        String[] responseLines = message.split("\n");

        if (responseLines.length == 1) 
        {
            outputStream.println(message + " ");
            return;
        }
        
        for (int i = 0; i < responseLines.length; i++)
        {
            if (i < responseLines.length-1) 
            {
                outputStream.println(responseLines[i] + "+");    
                continue;
            }

            outputStream.println(responseLines[i] + " ");
        }
    }
}
