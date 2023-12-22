package tp.vrp;

import tp.vrp.Data.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeUtil {

    /**
     * Converts a list of node IDs to a list of Node objects.
     *
     * @param nodeIds List of node IDs.
     * @param allNodes List of all available Node objects.
     * @return List of Node objects corresponding to the node IDs.
     */
    public static List<Node> idToNode(List<Integer> nodeIds, List<Node> allNodes) {
        List<Node> nodes = new ArrayList<>();
        for (int id : nodeIds) {
            Node node = findNodeById(id, allNodes);
            if (node != null) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * Finds a Node object by its ID.
     *
     * @param id Node ID to search for.
     * @param allNodes List of all Node objects.
     * @return The Node object with the specified ID, or null if not found.
     */
    public static Node findNodeById(int id, List<Node> allNodes) {
        for (Node node : allNodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    /**
     * Calculates the total distance for a given route represented by node IDs.
     *
     * @param nodeIds List of node IDs representing the route.
     * @param allNodes List of all Node objects.
     * @return The total distance of the route.
     */
    public static double totalDistance(List<Integer> nodeIds, List<Node> allNodes) {
        double totalDistance = 0.0;
        for (int i = 0; i < nodeIds.size() - 1; i++) {
            Node nodeA = findNodeById(nodeIds.get(i), allNodes);
            Node nodeB = findNodeById(nodeIds.get(i + 1), allNodes);
            if (nodeA != null && nodeB != null) {
                totalDistance += Node.GetDistance(nodeA, nodeB);
            }
        }
        totalDistance += Node.GetDistance(findNodeById(nodeIds.getFirst(), allNodes), findNodeById(nodeIds.getLast(), allNodes));

        return totalDistance;
    }

    public static double totalDistance2(List<Integer> nodeIds, List<Node> allNodes) {
        double totalDistance = 0.0;
        for (int i = 0; i < nodeIds.size() - 1; i++) {
            Node nodeA = findNodeById(nodeIds.get(i), allNodes);
            Node nodeB = findNodeById(nodeIds.get(i + 1), allNodes);
            if (nodeA != null && nodeB != null) {
                totalDistance += Node.GetDistance(nodeA, nodeB);
            }
        }
        return totalDistance;
    }

    /**
     * Reorders the list of nodes so that the depot node is at the beginning.
     *
     * @param nodes The list of node IDs.
     * @param allNodes The list of all nodes.
     * @param depotTypeId The type ID of the depot node.
     * @return The reordered list of node IDs with the depot at the start.
     */
    public static List<Integer> reorderListWithDepotFirst(List<Integer> nodes, List<Node> allNodes, int depotTypeId) {
        int depotIndex = -1;
        for (int i = 0; i < nodes.size(); i++) {
            Node node = findNodeById(nodes.get(i), allNodes);
            if (node != null && node.getType() == depotTypeId) {
                depotIndex = i;
                break;
            }
        }
        List<Integer> res = new ArrayList<>();

        for(int i =0; i<nodes.size(); i++)
        {
            res.add(nodes.get((depotIndex+i)%nodes.size()));
        }


        return res;
    }
    public static Node findStartNode(List<Node> nodes) {
        for (Node node : nodes) {
            if (node.type == 0) {
                return node;
            }
        }
        return null; // or handle this case appropriately
    }

    public static void printRouteResults(List<List<Integer>> solutions, List<Node> nodes) {
        double globalDistance = 0;
        for (int i = 0; i < solutions.size(); i++) {
            List<Integer> route = solutions.get(i);
            double distance = NodeUtil.totalDistance2(route, nodes);
            System.out.println("Route " + (i + 1) + ": " + route.toString());
            System.out.println("Distance: " + distance);
            System.out.println(); // For better readability
            globalDistance+=distance;
        }
        System.out.println("Global Route Distance : " + globalDistance);
    }



}
