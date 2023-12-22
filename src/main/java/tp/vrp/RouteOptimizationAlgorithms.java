package tp.vrp;


import tp.vrp.Data.Node;
import tp.vrp.NodeUtil;

import java.util.*;

public class RouteOptimizationAlgorithms {

    /**
     * Applies the 2-opt algorithm to improve an existing route.
     *
     * @param route The initial route.
     * @param nodes The list of all nodes.
     * @return An improved route.
     */
    private static List<Integer> applyTwoOpt(List<Integer> route, List<Node> nodes) {
        boolean improvement = true;
        while (improvement) {

            improvement = false;
            for (int i = 0; i < route.size() - 1; i++) {
                for (int k = i + 1; k < route.size(); k++) {

                    List<Integer> newRoute = twoOptSwap(route, i, k);
                    if (NodeUtil.totalDistance(newRoute, nodes) <
                            NodeUtil.totalDistance(route, nodes)) {
                        route = newRoute;
                        improvement = true;
                    }
                }
            }
        }
        return route;
    }


    /**
     * Performs a 2-opt swap by reversing the order of nodes between indices i and k.
     *
     * @param route The route to apply the swap to.
     * @param i     The start index of the segment to reverse.
     * @param k     The end index of the segment to reverse.
     * @return A new route with the segment reversed.
     */
    private static List<Integer> twoOptSwap(List<Integer> route, int i, int k) {
        List<Integer> newRoute = new ArrayList<>();
        // 1. Take route[0] to route[i-1] and add them in order to newRoute
        for (int c = 0; c <= i - 1; c++) {
            newRoute.add(route.get(c));
        }

        // 2. Take route[i] to route[k] and add them in reverse order to newRoute
        for (int c = k; c >= i; c--) {
            newRoute.add(route.get(c));
        }

        // 3. Take route[k+1] to end and add them in order to newRoute
        for (int c = k + 1; c < route.size(); c++) {
            newRoute.add(route.get(c));
        }

        return newRoute;
    }


    public static List<Integer> dumbHeuristic(List<Node> nodes) {
        Node startNode = NodeUtil.findStartNode(nodes);
        List<Node> unvisited = new ArrayList<>(nodes);
        List<Integer> path = new ArrayList<>();

        Node current = startNode;
        while (!unvisited.isEmpty()) {
            path.add(current.id);
            unvisited.remove(current);

            Node nextNode = findClosestNode(current, unvisited);
            if (nextNode == null) {
                break;
            }
            current = nextNode;
        }

        return path;
    }

    private static Node findClosestNode(Node current, List<Node> unvisited) {
        Node closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : unvisited) {
            double distance = Node.GetDistance(current, node);
            if (distance < minDistance) {
                minDistance = distance;
                closest = node;
            }
        }

        return closest;
    }


    /**
     * Applies Iterated Local Search with 2-opt as the local search method.
     *
     * @param initialRoute The initial route.
     * @param nodes        The list of all nodes.
     * @return An improved route.
     */
    public static List<Integer> iteratedLocalSearchWithTwoOpt(List<Integer> initialRoute, List<Node> nodes, int maxIter) {
        List<Integer> currentRoute = new ArrayList<>(initialRoute);
        List<Integer> bestRoute = currentRoute;
        double bestDistance = NodeUtil.totalDistance(currentRoute, nodes);

        for (int iteration = 0; iteration < maxIter; iteration++) {
            List<Integer> perturbedRoute = (perturbRoute(currentRoute));
            List<Integer> localOptimumRoute = applyTwoOpt(perturbedRoute, nodes);

            double perturbedDistance = NodeUtil.totalDistance(localOptimumRoute, nodes);
            if (perturbedDistance < bestDistance) {
                bestRoute = new ArrayList<>(perturbedRoute);
                bestDistance = perturbedDistance;
            }

            // Print the progress as a percentage
            double progress = (double) iteration / maxIter * 100;
            System.out.printf("Iteration %d of %d (%.2f%% complete)\n", iteration + 1, maxIter, progress);


            currentRoute = perturbedRoute;
        }

        return bestRoute;
    }


    /**
     * Slightly modifies the route to escape local optima.
     *
     * @param route The current route.
     * @return A perturbed route.
     */
    private static List<Integer> perturbRoute(List<Integer> route) {
        Random random = new Random();
        int index1 = random.nextInt(route.size());
        int index2 = random.nextInt(route.size());

        // Simple swap perturbation, can be more complex based on your problem
        Collections.swap(route, index1, index2);

        return route;
    }


    /**
     * Applies the GRASP methodology to the VRP.
     *
     * @param nodes         The list of all nodes.
     * @param maxIterations The number of iterations for the GRASP algorithm.
     * @return An improved route.
     */
    public static List<Integer> grasp(List<Node> nodes, int maxIterations) {
        List<Integer> bestRoute = null;
        double bestCost = Double.MAX_VALUE;
        Node depot = NodeUtil.findNodeById(101, nodes);

        for (int i = 0; i < maxIterations; i++) {
            List<Integer> initialSolution = greedyRandomizedConstruction(nodes, depot.id, 0.7);
            List<Integer> localOptimum = applyTwoOpt(initialSolution, nodes);
            double localOptimumCost = NodeUtil.totalDistance(localOptimum, nodes);

            if (localOptimumCost < bestCost) {
                bestRoute = new ArrayList<>(localOptimum);
                bestCost = localOptimumCost;
            }

            // Print the progress as a percentage
            double progress = (double) i / maxIterations * 100;
            System.out.printf("Iteration %d of %d (%.2f%% complete)\n", i + 1, maxIterations, progress);

        }

        return bestRoute;
    }


    /**
     * Constructs an initial solution for the VRP using a greedy randomized approach.
     *
     * @param nodes The list of all nodes.
     * @return A constructed route.
     */
    private static List<Integer> greedyRandomizedConstruction(List<Node> nodes, int depotId, double alpha) {
        List<Integer> route = new ArrayList<>();
        List<Node> candidateNodes = new ArrayList<>(nodes);

        // Remove the depot from candidate nodes to prevent adding it again
        candidateNodes.removeIf(node -> node.getId() == depotId);

        Random random = new Random();
        route.add(depotId); // Start the route at the depot

        Node lastNode = NodeUtil.findNodeById(depotId, nodes); // Find the depot node

        while (!candidateNodes.isEmpty()) {
            List<Node> rcl = createRestrictedCandidateList(lastNode, candidateNodes, alpha);
            Node selectedNode = rcl.get(random.nextInt(rcl.size()));
            route.add(selectedNode.getId());
            candidateNodes.remove(selectedNode);
            lastNode = selectedNode;
        }

        //route.add(depotId); // End the route at the depot
        return route;
    }

    /**
     * Creates a Restricted Candidate List (RCL) based on a criterion (e.g., distance).
     *
     * @param lastNode       The current route being constructed.
     * @param candidateNodes The list of nodes that have not yet been added to the route.
     * @param alpha          Parameter controlling the greediness and randomness.
     * @return The RCL.
     */
    private static List<Node> createRestrictedCandidateList(Node lastNode, List<Node> candidateNodes, double alpha) {
        Map<Node, Double> distanceMap = new HashMap<>();

        for (Node node : candidateNodes) {
            double distance = Node.GetDistance(lastNode, node);
            distanceMap.put(node, distance);
        }

        double minDistance = Collections.min(distanceMap.values());
        double maxDistance = Collections.max(distanceMap.values());
        double threshold = minDistance + alpha * (maxDistance - minDistance);

        List<Node> rcl = new ArrayList<>();
        for (Map.Entry<Node, Double> entry : distanceMap.entrySet()) {
            if (entry.getValue() <= threshold) {
                rcl.add(entry.getKey());
            }
        }

        return rcl;
    }


    public static List<List<Integer>> apply2OptOnSol(List<List<Integer>> solutions, List<Node> nodes) {
        for (int i = 0; i < solutions.size(); i++) {

            solutions.get(i).remove(solutions.get(i).size() - 1);
            solutions.set(i, NodeUtil.reorderListWithDepotFirst(applyTwoOpt(solutions.get(i), nodes), nodes, 0));
            solutions.get(i).add(101);
        }
        return solutions;
    }


}
