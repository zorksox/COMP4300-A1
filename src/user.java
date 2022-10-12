public class user 
{
    private static int maxId = 0;
    private String name;
    private int id;

    public user(String name)
    {
        this.name = name;
        id = ++maxId;
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }
}
