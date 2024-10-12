package chat;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatNode {
    private NodeInfo nodeInfo;
    private List<NodeInfo> peers;
    private Set<NodeInfo> joinedPeers; // Keep track of peers who have joined
    private ServerSocket serverSocket;

    public ChatNode(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        this.peers = new ArrayList<>();
        this.joinedPeers = new HashSet<>();
    }

    // Method to add peers
    public void addPeer(NodeInfo peer) {
        peers.add(peer);
    }

    // Start the node to listen for incoming connections
    public void start() throws IOException {
        serverSocket = new ServerSocket(nodeInfo.getPort());
        System.out.println("Node " + nodeInfo.getName() + " started on port " + nodeInfo.getPort());

        // Thread for handling incoming messages
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Send a message to a specific peer
    public void sendMessage(Message message, NodeInfo peer) throws IOException {
        if (joinedPeers.contains(peer) || message.getType() == MessageTypes.JOIN || message.getType() == MessageTypes.SHUTDOWN_ALL) {
            try (Socket socket = new Socket(peer.getAddress(), peer.getPort());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                out.writeObject(message);
            }
        }
    }

    // Send a message to all joined peers
    public void broadcastMessage(Message message) throws IOException {
        for (NodeInfo peer : peers) {
            sendMessage(message, peer);
        }
    }

    // Handle incoming connections
    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Handle incoming messages based on their type
    private void handleMessage(Message message) {
        switch (message.getType()) {
            case MessageTypes.JOIN:
                System.out.println(message.getSender() + " has joined the chat.");
                NodeInfo joiningPeer = findPeerByName(message.getSender());
                if (joiningPeer != null) {
                    joinedPeers.add(joiningPeer); // Mark the peer as joined
                }
                break;
            case MessageTypes.LEAVE:
                System.out.println(message.getSender() + " has left the chat.");
                NodeInfo leavingPeer = findPeerByName(message.getSender());
                if (leavingPeer != null) {
                    joinedPeers.remove(leavingPeer); // Remove the peer from the joined list
                }
                break;
            case MessageTypes.NOTE:
                System.out.println(message.getSender() + ": " + message.getContent());
                break;
            case MessageTypes.SHUTDOWN:
                System.out.println(message.getSender() + " has shut down.");
                break;
            case MessageTypes.SHUTDOWN_ALL:
                System.out.println("Shutting down all nodes.");
                System.exit(0);
                break;
            default:
        }
    }

    // Helper method to find a peer by name
    private NodeInfo findPeerByName(String name) {
        for (NodeInfo peer : peers) {
            if (peer.getName().equals(name)) {
                return peer;
            }
        }
        return null;
    }
}
