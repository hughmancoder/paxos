import paxos.*;
import utils.ElectionManager;
import utils.NodeSetupUtils;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/* Paxos implementation works when two councillors send voting proposals at
the same time */
public class SimultaneousMemberVoteTest {

    private List<PaxosNode> members;

    @BeforeEach
    public void setup() throws IOException {
        members = NodeSetupUtils.setupNodesFromFile("src/test/resources/simultaneous_vote.csv");
        assertNotNull(members, "Members should be initialized.");
    }

    @Test
    public void testSimultaneousMemberVote() throws InterruptedException, ExecutionException {
        ElectionManager manager = new ElectionManager(members);
        /* Adjust timer settings for the test */
        manager.getTimerUtils().setDelay(1000);
        manager.getTimerUtils().setPeriod(1000);

        manager.runElectionWithSimultaneousProposals();
        manager.waitForElectionToEnd(); // Wait for the election to conclude

        // After waiting for the election to end, check the consensus value
        Object consensusValue = manager.getConsensusValue();
        assertNotNull(consensusValue, "Consensus value should not be null after election.");

    }
}
