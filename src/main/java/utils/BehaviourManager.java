package utils;

import council.M2;
import council.M3;
import paxos.PaxosNode;
import java.util.List;

public class BehaviourManager {
    /*
     * Modifies behavour of council members according to the parameters passed in
     * according to their nature
     */
    public static List<PaxosNode> modifyMemberProperties(List<PaxosNode> members, boolean atCafe, int sendDelay,
            int receiveDelay, boolean isCamping) {
        for (PaxosNode member : members) {
            // Check if the member is an instance of M2 and modify its properties
            if (member instanceof M2) {
                M2 m2Member = (M2) member;
                m2Member.setAtCafe(atCafe);
                m2Member.setSendDelay(sendDelay);
                m2Member.setReceiveDelay(receiveDelay);
            }

            if (member instanceof M3) {
                M3 m3Member = (M3) member;
                m3Member.setIsCamping(isCamping);
            }
        }
        return members; // Return the modified list
    }
}
