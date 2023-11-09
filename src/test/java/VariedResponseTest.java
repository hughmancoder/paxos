import paxos.*;
import utils.BehaviourManager;
import utils.ElectionManager;
import utils.NodeSetupUtils;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/* – Paxos implementation works when M1 – M9 have responses to voting queries
suggested by several profiles (immediate response, small delay, large delay
and no response), including when M2 or M3 propose and then go offline */
public class VariedResponseTest {
        private List<PaxosNode> members;
        private ElectionManager manager;

        @BeforeEach
        public void setup() throws IOException {
                members = NodeSetupUtils.setupNodesFromFile("src/test/resources/varied_response_vote.csv");
                assertNotNull(members, "Members should be initialized.");
                manager = new ElectionManager(members);
                manager.getTimerUtils().setDelay(3000);
                manager.getTimerUtils().setPeriod(3000);
        }

        /*
         * Scenario: Small delays in responses.
         */
        @Test
        void testSmallDelayResponse() {
                members = BehaviourManager.modifyMemberProperties(members, true, 100, 100, false);

                manager.runElection();
                manager.waitForElectionToEnd();
                Object consensusValue = manager.getConsensusValue();
                assertNotNull(consensusValue, "Consensus value should not be null with small delay scenario.");
        }

        /*
         * Scenario: Large delays in responses.
         */
        @Test
        void testLargeDelayResponse() {
                members = BehaviourManager.modifyMemberProperties(members, false, 5000, 5000, true);

                manager.runElection();
                manager.waitForElectionToEnd();
                Object consensusValue = manager.getConsensusValue();
                assertNotNull(consensusValue, "Consensus value should not be null with large delay scenario.");
        }

        /*
         * Scenario: M2 or M3 propose and then go offline.
         */
        @Test
        void testOfflineAfterProposal() {

                // Simulate M2 or M3 going offline
                members = BehaviourManager.modifyMemberProperties(members, true, 300, 600, true);

                manager.runElection();
                manager.waitForElectionToEnd();
                Object consensusValue = manager.getConsensusValue();
                assertNotNull(consensusValue,
                                "Consensus value should not be null when proposers go offline after a proposal.");
        }

        /*
         * Scenario: Mixed response behaviors.
         */
        @Test
        void testMixedResponse() {
                // Generate random delays and camping status for testing mixed response
                // behaviour
                Random random = new Random();
                int randomSendDelay = random.nextInt(200); // Random delay up to 200ms
                int randomReceiveDelay = random.nextInt(200); // Random delay up to 200ms
                boolean randomCampingStatus = random.nextBoolean(); // Randomly determine camping status

                // Modify members with randomised properties to simulate mixed response
                // behaviour
                BehaviourManager.modifyMemberProperties(members, randomCampingStatus, randomSendDelay,
                                randomReceiveDelay, randomCampingStatus);

                manager.runElection();
                manager.waitForElectionToEnd();
                Object consensusValue = manager.getConsensusValue();
                assertNotNull(consensusValue, "Consensus value should not be null with mixed response scenario.");
        }

}
