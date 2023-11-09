package utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import models.Host;
import models.HostList;
import models.Message;

/* A generic implementation of paxosNode for testing networks */
public class PaxosTestNode {
    protected BlockingQueue<Message> messageQueue;
    protected NetworkHandler networkHandler;
    protected Host host;
    protected Boolean running;
    protected HostList hostList;

    public PaxosTestNode(Host host, HostList hostList) {

        this.networkHandler = new NetworkHandler(host);
        this.messageQueue = new LinkedBlockingQueue<>();
        this.host = host;
        this.hostList = hostList;
    }

    public void startNode() {
        this.networkHandler.startListening(this.messageQueue);
        this.networkHandler.startMessageProcessing();
        this.running = true;

    }

    public void send(Message message, Host targetHost) {
        this.networkHandler.sendMessage(message, targetHost);
    }

    // Getter to access received messages
    public Message getReceivedMessage() {
        try {
            return messageQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void broadcast(Message message) {
        for (Host targetHost : hostList.getHosts()) {
            if (!targetHost.equals(networkHandler.getLocalHost())) {
                networkHandler.sendMessage(message, targetHost);
            }
        }
    }

    public void stopNode() {
        running = false;
        this.networkHandler.stopListening();
        this.networkHandler.stopMessageProcessing();
    }

}