package chat;

import java.io.Serializable;
import java.util.Objects;

public class NodeInfo implements Serializable {
    private String name;
    private String address;
    private int port;

    public NodeInfo(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return port == nodeInfo.port && 
               Objects.equals(name, nodeInfo.name) && 
               Objects.equals(address, nodeInfo.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, port);
    }

    @Override
    public String toString() {
        return "NodeInfo{name='" + name + "', address='" + address + "', port=" + port + '}';
    }
}