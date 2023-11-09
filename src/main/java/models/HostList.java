package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HostList {

    private List<Host> hosts;

    public HostList() {
        hosts = new ArrayList<>();
    }

    public List<Host> getHosts() {
        return new ArrayList<>(hosts);
    }

    public List<Host> getProposerHosts() {
        return hosts.stream()
                .filter(host -> host.getRole() == PaxosRole.PROPOSER)
                .collect(Collectors.toList());
    }

    public List<Host> getAcceptorHosts() {
        return hosts.stream()
                .filter(host -> host.getRole() == PaxosRole.ACCEPTOR)
                .collect(Collectors.toList());
    }

    public List<Host> getLearnerHosts() {
        return hosts.stream()
                .filter(host -> host.getRole() == PaxosRole.LEARNER)
                .collect(Collectors.toList());
    }

    public void addHost(Host host) {
        hosts.add(host);
    }

    public int getSize() {
        return hosts.size();
    }

}
