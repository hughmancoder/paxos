package paxos;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import models.Host;
import models.HostList;
import models.Message;
import models.MessageType;
import models.PaxosRole;
import utils.NetworkHandler;

/**
 * Abstract class representing a node in the Paxos protocol.
 * Can act as a Learner, Proposer, or Acceptor depending on its current role.
 * It is implemented by Member classes
 */
public abstract class PaxosNode {
    protected NetworkHandler networkHandler;
    protected BlockingQueue<Message> messageQueue;
    protected Host host;
    protected HostList hostList;
    protected Boolean debug = false;
    private PaxosRole currentRole;
    protected final int majority;
    /* use volatile to ensure visibility across threads */
    protected volatile boolean running;
    protected volatile Object consensusValue;;

    Acceptor acceptor;
    Learner learner;
    Proposer proposer;

    /**
     * Constructor for PaxosNode.
     *
     * @param host     The host information for this node.
     * @param hostList A list of all hosts in the Paxos network.
     * @param debug    Flag to enable debug mode.
     */
    public PaxosNode(Host host, HostList hostList, Boolean debug) {
        this.networkHandler = new NetworkHandler(host);
        this.messageQueue = new LinkedBlockingQueue<>();
        this.host = host;
        this.hostList = hostList;
        this.running = true;
        this.consensusValue = null;
        this.majority = (hostList.getSize() / 2) + 1;
        this.debug = debug;
        this.currentRole = host.getRole();
        this.proposer = new Proposer(this);
        this.acceptor = new Acceptor(this);
        this.learner = new Learner(this);
    }

    /**
     * Overloaded constructor for PaxosNode without debug mode.
     *
     * @param host     The host information for this node.
     * @param hostList A list of all hosts in the Paxos network.
     */
    public PaxosNode(Host host, HostList hostList) {
        this(host, hostList, false);
    }

    /**
     * Starts the Paxos node, begins listening for messages and starts the election
     * process on a new thread to acheive distributed system functionality
     */
    public void startNode() {
        this.networkHandler.startListening(this.messageQueue);
        this.networkHandler.startMessageProcessing();
        new Thread(this::startElectionProcess, "ElectionProcessThread-" +
                host.getId()).start();
    }

    /**
     * Initiates an election round in the Paxos protocol if the node is a proposer
     * by sending a prepare request
     */
    public void initiateElection() {
        if (currentRole == PaxosRole.PROPOSER) {
            proposer.prepare();
        } else {
            System.out.println("INFO: Only proposers can initiate elections.");
        }
    }

    protected abstract void send(Message message, Host targetHost);

    protected abstract Message receive(Message message);

    /*
     * Primary loop which actively listens for incoming client messages and
     * delegates processing via handleMessage
     */
    private void startElectionProcess() {
        while (running) {
            try {
                Message message = messageQueue.take();
                handleMessage(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    /**
     * Processes the received message based on the node's current role.
     *
     * @param message The message to be handled.
     */
    private void handleMessage(Message message) {
        System.out.println("INFO: " + currentRole + " received message: " +
                message.toString());
        message = receive(message);
        if (message == null) {
            System.out.println("INFO: Received null message");
            return;
        }
        switch (currentRole) {
            case PROPOSER:
                delegateToProposer(message);
                break;
            case ACCEPTOR:
                delegateToAcceptor(message);
                break;
            case LEARNER:
                delegateToLearner(message);
                break;
            default:
                System.out.println("Error: Unhandled role: " + currentRole);
                break;
        }
    }

    void delegateToProposer(Message message) {
        switch (message.getType()) {
            case PROMISE:
                proposer.handlePromise(message);
                break;
            case NACK:
                proposer.handleNack(message);
                break;
            default:
                if (debug) {
                    System.out.println("INFO: Received an unhandled message type.");
                }
                break;
        }
    }

    void delegateToAcceptor(Message message) {
        switch (message.getType()) {
            case PREPARE:
                acceptor.handlePrepare(message);
                break;
            case PROPOSE:
                acceptor.handlePropose(message);
                break;
            case NACK:
                acceptor.handleNack(message);
                break;
            case ACCEPTED:
                learner.handleAccepted(message);
                break;
            default:
                if (debug) {
                    System.out.println("INFO: Unhandled message type: " + message.getType());
                }
                break;
        }
    }

    void delegateToLearner(Message message) {
        if (message.getType() == MessageType.ACCEPTED) {
            learner.handleAccepted(message);
        } else {
            System.out.println("LEARNER: Unhandled message type: " + message.getType());
        }
    }

    /* Stops the node's operation */
    public void stopNode() {
        running = false;
        this.networkHandler.stopListening();
        this.networkHandler.stopMessageProcessing();
    }

    protected void broadcast(Message message, List<Host> hosts) {
        for (Host targetHost : hosts) {
            if (!targetHost.equals(networkHandler.getLocalHost())) {
                networkHandler.sendMessage(message, targetHost);
            }
        }
    }

    public PaxosRole getRole(PaxosRole newRole) {
        return currentRole;
    }

    public void setRole(PaxosRole newRole) {
        currentRole = newRole;
    }

    public String getId() {
        return host.getId();
    }

    public Host getHost() {
        return host;
    }

    public int getAcceptorsQuorumSize() {
        return hostList.getAcceptorHosts().size();
    }

    public PaxosRole getRole() {
        return host.getRole();
    }

    public Boolean isRunning() {
        return this.running;
    }

    public Boolean hasReachedConsensus() {
        return this.consensusValue != null;
    }

    public Object getConsensusValue() {
        return consensusValue;
    }

    public void rest() {
        proposer.reset();
        acceptor.reset();
        learner.reset();
        messageQueue.clear();
        consensusValue = null;
    }

    /*
     * public void setHostList(HostList hostList) {
     * this.hostList = hostList;
     * }
     */

}
