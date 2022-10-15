import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class tests 
{
    static final String GREEN = "\u001B[32m";
    static final String RED = "\u001B[31m";
    static final String WHITE = "\u001B[37m";
    static String fromServer;
    static PrintWriter clientOutputStream;
    static Socket serverSocket;
    static PrintWriter serverOutputStream;
    static BufferedReader serverInputStream;
    static int passedTestCount = 0;
    static int failedTestCount = 0;

    public static void runTests(int portNumber, PrintWriter clientOutput)
    {
        clientOutputStream = clientOutput;
        serverSocket = connectToServer("localhost", portNumber);
        serverOutputStream = getServerPrintWriter(serverSocket);
        serverInputStream = getServerBufferedReader(serverSocket);

        try
        {
            while (((fromServer = serverInputStream.readLine()) != null))
            {
                //tests!
                connectionTest();
                emptyDatabaseTest();
                badRoomCreateTest();
                goodRoomCreateTest();
                duplicateRoomCreateTest();
                listTest();

                //final results
                respondToClient(GREEN + passedTestCount + " tests passed" + WHITE);
                respondToClient(RED + failedTestCount + " tests failed" + WHITE);
                clientOutputStream.println("Testing is complete. Exiting..." );
                System.exit(1);
                break;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }

        return;
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

    static void connectionTest()
    {
        if (fromServer.equals("Connected to server. Welcome User 1. "))
        {
            respondToClient(GREEN + "Connection test passed" + WHITE);
            passedTestCount++;
        }
        else 
        {
            respondToClient(RED + "Connection test failed. Output was: " + fromServer + WHITE);
            failedTestCount++;
        }
    }

    static void emptyDatabaseTest()
    {
        serverOutputStream.println("LIST");

        try 
        {
            fromServer = serverInputStream.readLine();

            if (fromServer.equals("No rooms exist "))
            {
                respondToClient(GREEN + "Empty database test passed" + WHITE);
                passedTestCount++;
            }
            else 
            {
                respondToClient(RED + "Empty database test failed" + WHITE);
                failedTestCount++;
            }
        } catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }

    static void badRoomCreateTest()
    {
        serverOutputStream.println("CREATE room a");

        try 
        {
            fromServer = serverInputStream.readLine();

            if (fromServer.equals("Missing or incorrect parameters. Usage: CREATE [room name] [capacity] "))
            {
                respondToClient(GREEN + "Incorrect room creation test passed" + WHITE);
                passedTestCount++;
            }
            else 
            {
                respondToClient(RED + "Incorrect room creation test failed" + WHITE);
                failedTestCount++;
            }
        } catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }

    static void goodRoomCreateTest()
    {
        serverOutputStream.println("CREATE room 6");

        try 
        {
            fromServer = serverInputStream.readLine();

            if (fromServer.equals("Room created: room "))
            {
                respondToClient(GREEN + "Room creation test passed" + WHITE);
                passedTestCount++;
            }
            else 
            {
                respondToClient(RED + "Room creation test failed" + WHITE);
                failedTestCount++;
            }
        } catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }

    static void duplicateRoomCreateTest()
    {
        serverOutputStream.println("CREATE room 7");

        try 
        {
            fromServer = serverInputStream.readLine();

            if (fromServer.equals("A chatroom with that name already exists. "))
            {
                respondToClient(GREEN + "Duplicate room creation test passed" + WHITE);
                passedTestCount++;
            }
            else 
            {
                respondToClient(RED + "Duplicate room creation test failed. Output was: " + fromServer + WHITE);
                failedTestCount++;
            }
        } catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }
    
    static void listTest()
    {
        serverOutputStream.println("LIST");

        try 
        {
            fromServer = serverInputStream.readLine();
            fromServer = serverInputStream.readLine();

            if (fromServer.equals("\troom\t\tCapacity: 0/6 "))
            {
                respondToClient(GREEN + "List test passed" + WHITE);
                passedTestCount++;
            }
            else 
            {
                respondToClient(RED + "List test failed. Output was: " + fromServer + WHITE);
                failedTestCount++;
            }
        } catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }

    static void respondToClient(String response)
    {
        clientOutputStream.println(response + "+");
    }
}
