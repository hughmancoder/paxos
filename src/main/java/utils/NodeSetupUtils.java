package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.*;
import paxos.*;

/* Setups up node from input csv file using factory method 
principle of creating objects without having to specify the 
exact class of the object that will be created. */
public class NodeSetupUtils {

    public static List<PaxosNode> setupNodesFromFile(String csvFilePath) {
        HostList hostList = new HostList();
        File file = new File(csvFilePath);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Host host = parseHost(line);
                if (host != null) {
                    hostList.addHost(host);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        List<PaxosNode> nodes = new ArrayList<>();
        for (Host host : hostList.getHosts()) {
            PaxosNode node = instantiateNode(host.getCouncillorType(), host, hostList);
            if (node != null) {
                node.startNode();
                nodes.add(node);
            }
        }

        return nodes;
    }

    private static Host parseHost(String line) {
        String[] parts = line.split(",");
        if (parts.length != 5) {
            System.err.println("Invalid host line: " + line);
            return null;
        }

        String address = parts[0].trim();
        int port = Integer.parseInt(parts[1].trim());
        CouncillorType councillorType = CouncillorType.valueOf(parts[2].trim());
        String id = parts[3].trim();
        PaxosRole role = PaxosRole.valueOf(parts[4].trim().toUpperCase());

        return new Host(address, port, id, role, councillorType);
    }

    private static PaxosNode instantiateNode(CouncillorType councillorType, Host host, HostList hostList) {
        try {
            Class<?> clazz = councillorType.getCouncillorClass();
            return (PaxosNode) clazz.getConstructor(Host.class, HostList.class).newInstance(host, hostList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
