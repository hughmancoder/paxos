package models;

import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;

    private final Host senderHost;
    private final MessageType type;
    private final int proposalNumber;
    private final Object proposedValue;
    private Integer highestSeenProposalNumber;

    public Message(MessageType type, int proposalNumber, Object proposedValue,
            Host senderHost) {
        this.type = type;
        this.proposalNumber = proposalNumber;
        this.proposedValue = proposedValue;
        this.senderHost = senderHost;
        this.highestSeenProposalNumber = 0;
    }

    public MessageType getType() {
        return type;
    }

    public int getProposalNumber() {
        return proposalNumber;
    }

    public Object getProposedValue() {
        return proposedValue;
    }

    public Host getSenderHost() {
        return senderHost;
    }

    public void setHighestSeenProposalNumber(Integer highestSeenProposalNumber) {
        this.highestSeenProposalNumber = highestSeenProposalNumber;
    }

    public String getSenderId() {
        return senderHost.getId();
    }

    public Integer getHighestSeenProposalNumber() {
        return highestSeenProposalNumber;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", proposalNumber=" + proposalNumber +
                ", proposedValue=" + proposedValue +
                ", senderId=" + senderHost.getId() +
                '}';
    }
}
