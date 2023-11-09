package paxos;

import java.util.HashSet;
import java.util.Set;

import models.Host;
import models.Message;
import models.MessageType;

public class Proposer {
    private int proposalNumber;
    private Object proposalValue;
    private Object acceptedValue;
    private int highestPromisedId;
    private Set<Host> promiseResponses;
    private PaxosNode paxosNode;

    public Proposer(PaxosNode paxosNode) {
        this.proposalNumber = 0;
        this.promiseResponses = new HashSet<>();
        highestPromisedId = -1;
        this.paxosNode = paxosNode;
    }

    /* Phase 1a: nominate self and send prepare message to all acceptor nodes */
    protected void prepare() {
        this.proposalValue = paxosNode.getHost().getId();
        proposalNumber++;
        Message prepareMessage = new Message(MessageType.PREPARE, proposalNumber,
                this.proposalValue, paxosNode.host);
        paxosNode.broadcast(prepareMessage, paxosNode.hostList.getAcceptorHosts());
    }

    protected void propose() {
        Object valueToSend = acceptedValue != null ? acceptedValue : proposalValue;
        Message proposeMessage = new Message(MessageType.PROPOSE, proposalNumber,
                valueToSend, paxosNode.host);
        paxosNode.broadcast(proposeMessage, paxosNode.hostList.getAcceptorHosts());
    }

    /* Phase 2a */
    protected void handlePromise(Message message) {
        // A promise from an acceptor has been received.
        Host sender = message.getSenderHost();
        if (!promiseResponses.contains(sender)) {
            promiseResponses.add(sender);
            int promisedId = message.getProposalNumber();
            if (promisedId > highestPromisedId) {
                highestPromisedId = promisedId;
                acceptedValue = message.getProposedValue();
            }

            int acceptorMajority = paxosNode.getAcceptorsQuorumSize() / 2 + 1;
            if (promiseResponses.size() >= acceptorMajority) {
                propose();
            }
        }
    }

    protected void handleNack(Message message) {
        Integer highestSeenProposalNumber = message.getHighestSeenProposalNumber();
        if (highestSeenProposalNumber != null && highestSeenProposalNumber >= this.proposalNumber) {
            this.proposalNumber = highestSeenProposalNumber + 1;
            promiseResponses.clear();
            this.acceptedValue = null;
        }
    }

    protected void reset() {
        this.proposalNumber = 0;
        this.promiseResponses.clear();
        highestPromisedId = -1;
    }
}
