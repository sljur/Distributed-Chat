package chat;

import java.io.Serializable;

public class Message implements Serializable {
    private int type;
    private String sender;
    private String content;

    public Message(int type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
