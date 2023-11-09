package paxos;

import models.Message;

public class Learner {

    private Object learnedValue;
    private boolean hasLearned;
    private PaxosNode paxosNode;

    public Learner(PaxosNode paxosNode) {
        this.paxosNode = paxosNode;
        this.hasLearned = false;
    }

    protected void handleAccepted(Message accept) {
        if (!hasLearned) {
            learnedValue = accept.getProposedValue();
            hasLearned = true;
            if (paxosNode.debug) {
                System.out
                        .println(
                                "LEARN: Learner with ID " + paxosNode.host.getId() + " learned value: " +
                                        learnedValue);

            }
            paxosNode.consensusValue = learnedValue;
            broadcastLearnedValue(accept);
        }
    }

    protected void broadcastLearnedValue(Message learned) {
        paxosNode.broadcast(learned, paxosNode.hostList.getHosts());
    }

    protected void reset() {
        this.hasLearned = false;
    }
}