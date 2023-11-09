import paxos.*;
import utils.ElectionManager;
import utils.NodeSetupUtils;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.util.List;

/*
 * Paxos implementation works in the case where all M1-M9 have immediate
 * responses to voting queries
 */
public class ImmediateResponseTest {
    private List<PaxosNode> members;
    private ElectionManager manager;

    @BeforeEach
    public void setup() throws IOException {
        members = NodeSetupUtils.setupNodesFromFile("src/test/resources/simultaneous_vote.csv");
        assertNotNull(members, "Members should be initialized.");
        manager = new ElectionManager(members);
    }

    @Test
    void testImmediateResponse() {
        // Adjust timer settings for the test
        manager.getTimerUtils().setDelay(1000);
        manager.getTimerUtils().setPeriod(1000);

        manager.runElection();
        manager.waitForElectionToEnd();
        Object consensusValue = manager.getConsensusValue();
        assertNotNull(consensusValue, "Consensus value should not be null after election.");
    }
}
