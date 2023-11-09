package council;

import models.*;
import paxos.PaxosNode;
import java.util.Random;

/* M3 has also wanted to be council president. M3 is not as responsive as M1, nor as late as M2, 
however sometimes emails completely do not get to M3. The other councillors suspect that it’s 
because sometimes M3 goes camping in the Coorong, completely disconnected from the world. */
public class M3 extends PaxosNode {

    private boolean isCamping;
    private Random random;

    public M3(Host host, HostList hostList) {
        super(host, hostList);
        this.isCamping = false;
        this.random = new Random();
    }

    public void setIsCamping(boolean isCamping) {
        this.isCamping = isCamping;
    }

    @Override
    protected void send(Message message, Host targetHost) {

        if (!isCamping) {
            if (random.nextDouble() > 0.1) { // 90% chance to send the message
                this.networkHandler.sendMessage(message, targetHost);
            } else {
                // 10% chance the message doesn't get sent
                System.out.println("INFO: M3 failed to send the message, possibly camping in the Coorong!");
            }
        } else {
            // Completely skip sending messages when camping
            System.out.println("INFO: M3 is currently camping and cannot send messages");
        }
    }

    @Override
    protected Message receive(Message message) {
        if (!isCamping) {
            return message;
        } else {
            // Return null to indicate the message wasn't received
            System.out.println("INFO: M3 is currently camping and cannot receive messages.");
            return null;
        }
    }
}
