import java.util.LinkedList;

public class chatroom 
{
    private String name;
    private int capacity;
    private LinkedList<user> currentUsers;
    private LinkedList<message> messages;

    public chatroom(String name, int capacity)
    {
        this.name = name;
        this.capacity = capacity;
        currentUsers = new LinkedList<user>();
        messages = new LinkedList<message>();
    }

    public String getName()
    {
        return name;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void postMessage(String message, user user)
    {
        for (user u : currentUsers)
        {
            u.sendMessage(message);
        }
    }

    public int getCurrentUserCount()
    {
        return currentUsers.size();
    }

    public void addUser(user user)
    {
        currentUsers.add(user);
    }

    public void removeUser(user user)
    {
        currentUsers.remove(user);
    }
}


