package chat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class ChatApp {
    private static boolean isJoined = false;  // Track whether the user has joined

    public static void main(String[] args) throws IOException {
        // Check if a command-line argument is provided for the properties file
        if (args.length < 1) {
            System.out.println("Usage: java chat.ChatApp <properties_file_path>");
            System.exit(1);
        }

        // Load configuration from the properties file provided in the command-line argument
        String propertiesFilePath = args[0];
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(propertiesFilePath)) {
            props.load(in);
        }

        // Get own node info from the properties file
        String myName = props.getProperty("MY_NAME");
        int myPort = Integer.parseInt(props.getProperty("MY_PORT"));

        NodeInfo myNodeInfo = new NodeInfo(myName, "localhost", myPort);
        ChatNode chatNode = new ChatNode(myNodeInfo);
        chatNode.start();

        // Add multiple peers from the configuration file
        int peerCount = 1;
        while (true) {
            String peerName = props.getProperty("PEER_" + peerCount + "_NAME");
            String peerIP = props.getProperty("PEER_" + peerCount + "_IP");
            String peerPortStr = props.getProperty("PEER_" + peerCount + "_PORT");
            
            if (peerName == null || peerIP == null || peerPortStr == null) {
                break;  // No more peers to add
            }

            int peerPort = Integer.parseInt(peerPortStr);
            NodeInfo peer = new NodeInfo(peerName, peerIP, peerPort);
            chatNode.addPeer(peer);
            peerCount++;
        }

        // Keep the application running for future message sending (e.g., notes, leave, etc.)
        handleUserInput(chatNode, myName);
    }

    // This method handles the user input and sends messages based on the input type.
    private static void handleUserInput(ChatNode chatNode, String myName) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Read the command from the user
            String type = scanner.nextLine().trim();

            try {
                // Handle the SHUTDOWN command (shuts down this node)
                if (type.equals("SHUTDOWN")) {
                    System.out.println("Shutting down this node...");
                    System.exit(0);
                }

                // Handle the SHUTDOWN_ALL command (broadcasts shutdown to all nodes and exits)
                if (type.equals("SHUTDOWN_ALL")) {
                    chatNode.broadcastMessage(new Message(MessageTypes.SHUTDOWN_ALL, myName, ""));
                    System.out.println("Shutting down all nodes...");
                    System.exit(0);
                }

                // Ensure the user has joined before sending any other messages
                if (!isJoined && !type.equals("JOIN")) {
                    System.out.println("You must join the chat first by typing 'JOIN'.");
                    continue;
                }

                // Handle the JOIN command
                else if (type.equals("JOIN")) {
                    chatNode.broadcastMessage(new Message(MessageTypes.JOIN, myName, ""));
                    isJoined = true;  // Mark this user as joined after sending the JOIN message
                }

                // Handle the LEAVE command
                else if (type.equals("LEAVE")) {
                    chatNode.broadcastMessage(new Message(MessageTypes.LEAVE, myName, ""));
                    isJoined = false;  // Mark this user as not joined after leaving
                }
                // Handle NOTE command
                else {
                    String content = type.substring(0); // Get the content after "NOTE "
                    chatNode.broadcastMessage(new Message(MessageTypes.NOTE, myName, content));
                }
            } catch (IOException e) {
                System.err.println("Failed to send message: " + e.getMessage());
            }
        }
    }
}
