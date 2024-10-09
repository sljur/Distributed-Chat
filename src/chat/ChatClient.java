package chat;

import java.io.IOException;

import utils.PropertyHandler;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import utils.NetworkUtilities;


/**
 * ChatClient class to act as both server and client
 */
public class ChatClient implements Runnable
{
    // create linked list of participants here


    //refrence to receiver/sender
    static Receiver receiver = null;
    static Sender sender = null; 

    // create the server socket
    private ServerSocket serverSocket = null;

    // this client node's connectivity information
    public static NodeInfo myNodeInfo = null;


    // create chat client with contructor
    public ChatClient(String propertiesFile)
    {
        // get properties from properties file
        Properties properties = null;
        try
        {
            properties = new PropertyHandler(propertiesFile);
        }
        catch(IOException ex)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not open properties file", ex);
            System.exit(1);
        }

        // get my Reciever port number
        int myPort = 0;
        try
        {
            myPort = Integer.parseInt(properties.getProperty("MY_PORT"));
        }
        catch(NumberFormatException ex)
        {
             Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read receiver port", ex);
             system.exit(1);
        }

        // get my name
        String myName = properties.getProperty("MY_NAME");
        if(myName == null)
        {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read my name");
            System.exit(1);
        }

        // create my own node info
        myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), myPort, myName); 

    }

    // code entry point for server and client functionality
    @Override
    public void run()
    {
        // start server thread to accept incoming connections
        Thread listenerThread = new Thread(() -> startListener());
        listenerThread.start();

        //start the receiver
        (receiver = new Receiver()).start();

        // start the sender
        (sender = new Sender()).start();
    }

    // Method to start the server to accept incoming connections
    // called by the thread above
    private void startListener() {
        try {
            serverSocket = new ServerSocket(myPort);
            System.out.println("Listening on " + NetworkUtilities.getMyIP() + ":" + myPort);
        } catch (IOException ex) {
            Logger.getLogger(ChatNode.class.getName()).log(Level.SEVERE, "Cannot open server socket", ex);
            System.exit(1);
        }

        // Server loop to accept new connections
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                // Add the new client to participants and start a handler thread
                participants.add(new NodeInfo(clientSocket.getInetAddress().toString(), clientSocket.getPort()));
                (new ChatServerWorker(clientSocket)).start();
            } catch (IOException e) {
                System.out.println("Warning: Error accepting client");
            }
        }
    }

    // main()
    public static void main(String[] args)
    {
        String propertiesFile = null;

        try
        {
            propertiesFile = args[0];
        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
                            // will probably have to change this path
            propertiesFile = "config/ChatNodeDefaults.properties";
        }

        // start ChatNode
        (new ChatClient(propertiesFile)).run();
    }
}
