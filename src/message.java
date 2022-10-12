import java.time.Instant;

public class message 
{
    private String content;
    private int posterId;
    private Instant timeStamp; 

    public message(String content, int posterId)
    {
        this.content = content;
        this.posterId = posterId;
        timeStamp = Instant.now();
    }

    public String getContent()
    {
        return content;
    }

    public int getPosterId()
    {
        return posterId;
    }

    public String getTimeStamp()
    {
        String timeStampString = timeStamp.toString();
        String timeStampDate = timeStampString.substring(0,9);
        String timeStampTime = timeStampString.substring(11, timeStampString.length()-2);
        return timeStampDate + " " + timeStampTime;
    }
}
