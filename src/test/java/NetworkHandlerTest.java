import utils.PaxosTestNode;
import models.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

class NetworkHandlerTest {

    private PaxosTestNode memberA;
    private PaxosTestNode memberB;
    private PaxosTestNode memberC;

    HostList hostList;
    Host hostA;
    Host hostB;
    Host hostC;

    @BeforeEach
    void setUp() throws IOException {
        String address = "127.0.0.1";
        hostA = new Host(address, 4570, "IdA", PaxosRole.ACCEPTOR);
        hostB = new Host(address, 4571, "IdB", PaxosRole.ACCEPTOR);
        hostC = new Host(address, 4572, "IdC", PaxosRole.ACCEPTOR);

        hostList = new HostList();
        hostList.addHost(hostA);
        hostList.addHost(hostB);
        hostList.addHost(hostC);

        memberA = new PaxosTestNode(hostA, hostList);
        memberB = new PaxosTestNode(hostB, hostList);
        memberC = new PaxosTestNode(hostC, hostList);

        System.out.println("INFO: starting election process");

        memberA.startNode();
        memberB.startNode();
        memberC.startNode();

    }

    @AfterEach
    void tearDown() {
        System.out.println("INFO: stopping messages");
        memberA.stopNode();
        memberB.stopNode();
        memberC.stopNode();
    }

    @Test
    void ExchangeMessages() throws InterruptedException {

        System.out.println("INFO: testing ExchangeMessages");

        MessageType messageType = MessageType.PROPOSE;

        Message messageFromA = new Message(messageType, 1,
                "Value from A", hostA);
        Message messageFromB = new Message(messageType, 2,
                "Value from B", hostB);

        Assertions.assertNotNull(hostB, "hostB is null before sendMessage");
        memberA.send(messageFromA, hostB);

        Assertions.assertNotNull(hostA, "hostA is null before sendMessage");
        memberB.send(messageFromB, hostA);

        Message receivedByB = memberB.getReceivedMessage();
        Message receivedByA = memberA.getReceivedMessage();

        assertEquals(receivedByB.toString(), messageFromA.toString());
        assertEquals(receivedByA.toString(), messageFromB.toString());

    }

    /* Broadcast message from memberA to members B and C */
    @Test
    void BroadcastMessage() throws InterruptedException {
        System.out.println("INFO: testing BroadCastMessage");
        Message message = new Message(MessageType.ACCEPTED, 3, "Value", hostA);
        memberA.broadcast(message);

        Thread.sleep(1000);

        Message receivedByB = memberB.getReceivedMessage();
        Message receivedByC = memberC.getReceivedMessage();
        assertEquals(receivedByB.toString(), message.toString());
        assertEquals(receivedByC.toString(), message.toString());
    }
}
