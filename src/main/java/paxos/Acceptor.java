
package paxos;

import models.*;

public class Acceptor {

    private Object acceptedValue;
    private int acceptedId;
    private int maxIdSeen;
    private PaxosNode paxosNode;

    public Acceptor(PaxosNode paxosNode) {
        this.maxIdSeen = -1;
        this.paxosNode = paxosNode;
    }

    /* Phase 1b */
    protected void handlePrepare(Message message) {
        int proposalId = message.getProposalNumber();
        if (proposalId > maxIdSeen) {
            maxIdSeen = proposalId;
            Message promise = new Message(MessageType.PROMISE, maxIdSeen,
                    message.getProposedValue(), paxosNode.host);
            paxosNode.send(promise, message.getSenderHost());
        }
    }

    /* Phase 2b: */
    protected void handlePropose(Message message) {
        int proposalId = message.getProposalNumber();
        Object value = message.getProposedValue();

        if (proposalId >= maxIdSeen) {
            maxIdSeen = proposalId;
            acceptedId = proposalId;
            acceptedValue = value;
            // Send an ACCEPTED message back to the proposer and to all learners
            Message accepted = new Message(MessageType.ACCEPTED, acceptedId,
                    acceptedValue, paxosNode.host);
            paxosNode.send(accepted, message.getSenderHost());
            broadcastToLearners(accepted);

        }
    }

    protected void broadcastToLearners(Message accepted) {
        paxosNode.broadcast(accepted, paxosNode.hostList.getLearnerHosts());
    }

    protected void handleNack(Message message) {
        int proposalId = message.getProposalNumber();
        maxIdSeen = proposalId;

    }

    protected void reset() {
        this.maxIdSeen = -1;
    }

}
