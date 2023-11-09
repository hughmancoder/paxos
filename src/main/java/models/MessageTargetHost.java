package models;

/* pair a message with its target host to keep track of incoming and outgoing
communciation routes*/
public class MessageTargetHost {
    private final Message message;
    private final Host targetHost;

    public MessageTargetHost(Message message, Host targetHost) {
        this.message = message;
        this.targetHost = targetHost;
    }

    public Message getMessage() {
        return message;
    }

    public Host getTargetHost() {
        return targetHost;
    }
}
