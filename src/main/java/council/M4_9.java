package council;

import models.*;
import paxos.PaxosNode;
import java.util.Random;

/* Members M4-M9 have no particular ambitions about council presidency and no particular preferences or animosities, 
so they will try to vote fairly. Their jobs keep them fairly busy and as such their response times  will vary. */
public class M4_9 extends PaxosNode {

    private Random random;

    public M4_9(Host host, HostList hostList) {
        super(host, hostList);
        this.random = new Random();
    }

    @Override
    protected void send(Message message, Host targetHost) {
        new Thread(() -> {
            try {
                int delay = getRandomDelay();
                Thread.sleep(delay);
                this.networkHandler.sendMessage(message, targetHost);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    protected Message receive(Message message) {
        try {
            // Simulate busy schedule by random delay
            int delay = getRandomDelay();
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return message;
    }

    /* Return a random delay, for example, between 0 and 5 seconds */
    private int getRandomDelay() {
        return random.nextInt(5000);
    }
}
