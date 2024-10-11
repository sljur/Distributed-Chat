package chat;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;

import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Represents the reciever side of a chat client that receives messages
 * from the chat server
 * 
 * @author wolfdieterotte
 */

public class Receiver extends Thread
{
    NodeInfoList connections;

    static String userName = null;

    /**
     * Constructor - will need to receive linked list from ChatClient as argument
     */
    public Receiver(NodeInfoList connections)
    {
        this.connections = connections;

        // loop through linked list of connections
        for (NodeInfo myNodeInfo : connections) {
            try
            {
                // create a socket for each node in the list
                ServerSocket receiverSocket = new ServerSocket(myNodeInfo.getPort());
                System.out.println("[Receiver.Receiver] receiver socket created, listening on port " + myNodeInfo.getPort());
            }
            catch(IOException ex)
            {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, "Creating receiver socket failed", ex);
            }

            System.out.println(myNodeInfo.getName() + " Listening on " + myNodeInfo.getAddress() + ":" +
                nodeInfo.getPort());
        }
    }


    // thread entry point
    @Override
    public void run()
    {
        // run server loop
        while(true)
        {
            // loop through the list of connections
            for (NodeInfo myNodeInfo : connections) {
                try
                {
                    // Create a new ServerSocket for accepting connections
                    ServerSocket receiverSocket = new ServerSocket(myNodeInfo.getPort());

                    // accept connection
                    (new ReceiverWorker(receiverSocket.accept())).start();
                }
                catch(IOException e)
                {
                    System.out.println("[Receiver.run] Warning: Error accepting client");
                }
            }
        }
    }
}
