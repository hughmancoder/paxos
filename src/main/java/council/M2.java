package council;

import models.*;
import paxos.PaxosNode;
import java.util.Random;

/* M2 has also wanted to be council president for a very long time, 
except their very long time is longer than everybody else's. M2 lives in the Adelaide Hills ]
and thus their internet connection is really poor, almost non-existent. Responses to emails come in very late, 
and sometimes only to one of the emails in the email thread, so it is unclear whether M2 has read/understood them all. 
However, M2 sometimes likes to work at Sheoak Café.  When that happens, their responses are instant and M2 replies to all emails. */
public class M2 extends PaxosNode {

    private boolean atCafe;
    private Random randomDelay;

    private int sendDelay;
    private int receiveDelay;

    public M2(Host host, HostList hostList) {
        super(host, hostList);
        this.atCafe = false;
        this.randomDelay = new Random();
        this.sendDelay = 5000; // default delay for sending messages
        this.receiveDelay = 5000; // default delay for receiving messages
    }

    public void setAtCafe(boolean atCafe) {
        this.atCafe = atCafe;
    }

    // Set custom delay for sending messages
    public void setSendDelay(int delay) {
        this.sendDelay = delay;
    }

    // Set custom delay for receiving messages
    public void setReceiveDelay(int delay) {
        this.receiveDelay = delay;
    }

    @Override
    protected void send(Message message, Host targetHost) {
        // When at the café, send messages instantly
        if (atCafe) {
            this.networkHandler.sendMessage(message, targetHost);
        } else {
            // Simulate a delay when not at the café
            new Thread(() -> {
                try {

                    int delay = randomDelay.nextInt(sendDelay) + 1000; // 1 second + random delay
                    Thread.sleep(delay);
                    Thread.sleep(delay);
                    this.networkHandler.sendMessage(message, targetHost);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    @Override
    protected Message receive(Message message) {
        if (!atCafe) {
            // Simulate a delay in receiving the message when not at the café.
            try {
                int delay = randomDelay.nextInt(receiveDelay) + 1000; // 1 second + random delay
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Whether at the café or not, the message is the same when received.
        return message;
    }
}
