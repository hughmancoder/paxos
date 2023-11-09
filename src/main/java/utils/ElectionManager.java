package utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

import models.PaxosRole;
import paxos.PaxosNode;

/**
 * Manages the election process in a Paxos cluster.
 */
public class ElectionManager {
    private final List<PaxosNode> nodes;
    private final TimerUtils timerUtils;
    private Object consensusValue;
    private ScheduledExecutorService scheduler;
    private final CountDownLatch electionConcludedLatch = new CountDownLatch(1);

    /**
     * Constructor for ElectionManager.
     * 
     * @param nodes The list of Paxos nodes.
     */
    public ElectionManager(List<PaxosNode> nodes) {
        this.nodes = nodes;
        this.timerUtils = new TimerUtils(10000, 10000);
        this.consensusValue = null;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Starts the election process by initiating the proposal phase on a single
     * proposer. A timer periodically checks if consensus has been reached and
     * monitors election progression
     */
    public void runElection() {
        List<PaxosNode> proposerNodes = getProposers(nodes);
        if (proposerNodes.isEmpty()) {
            logNoProposersAvailable();
            return;
        }

        PaxosNode proposer = proposerNodes.get(0);
        try {
            proposer.initiateElection();
            startConsensusCheck();
        } catch (Exception e) {
            handleElectionError(e);
        }
    }

    /**
     * Starts the election process with multiple proposers initiating the proposal
     * phase simultaneously. Used for testing
     */
    public void runElectionWithSimultaneousProposals() {
        List<PaxosNode> proposerNodes = getProposers(nodes);
        if (proposerNodes.isEmpty()) {
            logNoProposersAvailable();
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(proposerNodes.size());
        CompletableFuture<?>[] futures = new CompletableFuture[proposerNodes.size()];

        System.out.println(
                "INFO: starting an election with " + proposerNodes.size() + " proposers voting simultaneously");

        int index = 0;
        for (PaxosNode proposer : proposerNodes) {
            futures[index] = CompletableFuture.runAsync(() -> {
                try {
                    proposer.initiateElection();
                } catch (Exception e) {
                    handleElectionError(e);
                }
            }, executor);
            index++;
        }

        CompletableFuture.allOf(futures).join();
        startConsensusCheck();
        executor.shutdown();
    }

    private boolean hasElectionConcluded() {
        List<PaxosNode> learnerNodes = getLearners(nodes);
        for (PaxosNode learner : learnerNodes) {
            if (learner.hasReachedConsensus()) {
                consensusValue = learner.getConsensusValue();
                return true;
            }
        }
        return false;
    }

    private void startConsensusCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            if (hasElectionConcluded()) {
                handleElectionSuccess();
                scheduler.shutdownNow(); // Stop further checks after success
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private Object handleElectionSuccess() {
        System.out.println("Consensus achieved for value " + consensusValue);
        concludeElection();
        electionConcludedLatch.countDown();
        return consensusValue;
    }

    private void logNoProposersAvailable() {
        System.out.println("No proposers available to run the election.");
    }

    private void handleElectionError(Exception e) {
        System.out.println("Election error occurred: " + e.getMessage());

    }

    private void concludeElection() {
        System.out.println("INFO: concluding election");
        timerUtils.stop();
        scheduler.shutdown();
        for (PaxosNode node : nodes) {
            node.stopNode();
        }
    }

    public void waitForElectionToEnd() {
        try {
            electionConcludedLatch.await();
        } catch (InterruptedException e) {
            System.err.println("Election wait was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            System.exit(0);
        }
    }

    /* GETTERS */
    public TimerUtils getTimerUtils() {
        return timerUtils;
    }

    public Object getConsensusValue() {
        return consensusValue;
    }

    public static List<PaxosNode> getProposers(List<PaxosNode> nodes) {
        return nodes.stream()
                .filter(node -> PaxosRole.PROPOSER.equals(node.getRole()))
                .collect(Collectors.toList());
    }

    public static List<PaxosNode> getAcceptors(List<PaxosNode> nodes) {
        return nodes.stream()
                .filter(node -> PaxosRole.ACCEPTOR.equals(node.getRole()))
                .collect(Collectors.toList());
    }

    public static List<PaxosNode> getLearners(List<PaxosNode> nodes) {
        return nodes.stream()
                .filter(node -> PaxosRole.LEARNER.equals(node.getRole()))
                .collect(Collectors.toList());
    }

    /*
     * public static List<PaxosNode> delegateNewRoles(List<PaxosNode> nodes) {
     * return nodes;
     * }
     */
}