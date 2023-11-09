import java.util.Arrays;
import java.util.List;

import council.*;
import paxos.*;
import models.*;
import utils.ElectionManager;

class Main {

    public static List<PaxosNode> setupNodes() {
        HostList hostList = new HostList();
        String address = "127.0.0.1";

        Host host1 = new Host(address, 4570, "M1-1", PaxosRole.PROPOSER);
        Host host2 = new Host(address, 4571, "M1-2", PaxosRole.ACCEPTOR);
        Host host3 = new Host(address, 4572, "M1-3", PaxosRole.LEARNER);

        hostList = new HostList();
        hostList.addHost(host1);
        hostList.addHost(host2);
        hostList.addHost(host3);

        PaxosNode member1 = new M1(host1, hostList);
        PaxosNode member2 = new M1(host2, hostList);
        PaxosNode member3 = new M1(host3, hostList);

        member1.startNode();
        member2.startNode();
        member3.startNode();

        return Arrays.asList(member1, member2, member3);
    }

    public static void main(String[] args) {
        List<PaxosNode> members = setupNodes();
        ElectionManager manager = new ElectionManager(members);
        manager.runElection();
        manager.waitForElectionToEnd();
    }

}
