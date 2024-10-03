package chat;

import java.util.LinkedList;

public class NodeInfoList {
    
    // variables
    private LinkedList<NodeInfo> nodeList;
    
    /*
    * Constructor 
    */
    public NodeInfoList() 
    {
        nodeList = new LinkedList<>();
    }
    
    /*
    * @function addNode
    *
    * @parameter NodeInfo node
    */
    public void addNode(NodeInfo node) {
        nodeList.add(node);
    }
    
    /*
    * @function removeNode
    *
    * @parameter NodeInfo node
    */
    public boolean removeNode(NodeInfo node) {
        return nodeList.remove(node);
    }
    
    /*
    * @function getNode
    *
    * @parameter int index
    */
    public NodeInfo getNode(int index) {
        if (index >= 0 && index < nodeList.size()) {
            return nodeList.get(index);
        }
        return null;
    }
    
    /*
    * @function displayNodes
    *
    * @parameter None
    */
    public void displayNodes() {
        for (NodeInfo node : nodeList) {
            System.out.println("Name: " + node.getName() + ", Address: " + node.getAddress() + ", Port: " + node.getPort());
        }
    }
}