package council;

import models.*;
import paxos.PaxosNode;

/* Member M1 – M1 has wanted to be council president for a very long time.
M1 is very chatty over social media and responds to emails/texts/calls almost
instantly.
It is as if M1 has an in-brain connection with their mobile phone! */
public class M1 extends PaxosNode {

    public M1(Host host, HostList hostList) {
        super(host, hostList);
    }

    @Override
    protected void send(Message message, Host targetHost) {
        this.networkHandler.sendMessage(message, targetHost);
    }

    @Override
    protected Message receive(Message message) {
        return message;
    }
}