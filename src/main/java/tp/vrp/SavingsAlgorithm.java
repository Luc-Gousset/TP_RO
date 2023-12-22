package tp.vrp;

import tp.vrp.Data.Node;

import java.util.*;

public class SavingsAlgorithm {

    private List<Node> nodes;
    private Node depot;
    private List<List<Node>> routes;

    public SavingsAlgorithm(List<Node> nodes, Node depot) {
        this.nodes = new ArrayList<>(nodes);
        this.depot = depot;
        this.routes = new ArrayList<>();
    }

    public List<List<Node>> calculateRoutes() {
        List<Saving> savings = calculateSavings();

        // Sort savings in descending order
        savings.sort(Comparator.comparingDouble(Saving::getAmount).reversed());

        for (Saving saving : savings) {
            // Try to combine routes for each pair of nodes
            combineRoutes(saving);
        }

        return routes;
    }

    private List<Saving> calculateSavings() {
        List<Saving> savings = new ArrayList<>();
        for (Node nodeA : nodes) {
            for (Node nodeB : nodes) {
                if (!nodeA.equals(nodeB)) {
                    double savingAmount = Node.GetDistance(depot, nodeA) +
                            Node.GetDistance(depot, nodeB) -
                            Node.GetDistance(nodeA, nodeB);
                    savings.add(new Saving(nodeA, nodeB, savingAmount));
                }
            }
        }
        return savings;
    }

    private void combineRoutes(Saving saving) {
        // Logic to combine routes based on saving without violating constraints
        // This is quite complex and involves checking if nodes are already in a route,
        // whether they can be combined without exceeding capacity, etc.
    }

    private static class Saving {
        private Node nodeA;
        private Node nodeB;
        private double amount;

        public Saving(Node nodeA, Node nodeB, double amount) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.amount = amount;
        }

        public double getAmount() {
            return amount;
        }

        // Getters and setters
    }
}
