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
     * @param maxIter The maximum number of iterations to perform.
     * @param maxIterWithoutImprovement The maximum number of iterations without improvement before stopping.
     * @return An improved route.
     */
    public static List<Integer> iteratedLocalSearchWithTwoOpt(List<Integer> initialRoute, List<Node> nodes, int maxIter, int maxIterWithoutImprovement) {
        List<Integer> currentRoute = new ArrayList<>(initialRoute);
        List<Integer> bestRoute = new ArrayList<>(currentRoute);
        double bestDistance = NodeUtil.totalDistance(currentRoute, nodes);

        int lastImprovementIteration = 0; // Initialize the last improvement iteration

        for (int iteration = 0; iteration < maxIter; iteration++) {
            List<Integer> perturbedRoute = perturbRoute(currentRoute, iteration);
            List<Integer> localOptimumRoute = applyTwoOpt(perturbedRoute, nodes);

            double perturbedDistance = NodeUtil.totalDistance(localOptimumRoute, nodes);
            if (perturbedDistance < bestDistance) {
                bestRoute = new ArrayList<>(localOptimumRoute);
                bestDistance = perturbedDistance;
                lastImprovementIteration = iteration; // Update the last improvement iteration
            }

            // Stop if no improvement is seen for maxIterWithoutImprovement iterations
            if (iteration - lastImprovementIteration >= maxIterWithoutImprovement) {
                System.out.println("Stopping early due to no improvement.");
                break;
            }

            // Print the progress as a percentage
            double progress = (double) iteration / maxIter * 100;
            System.out.printf("Iteration %d of %d (%.2f%% complete)  distance : %f\n", iteration + 1, maxIter, progress, bestDistance);

            currentRoute = new ArrayList<>(localOptimumRoute); // Update currentRoute to the locally optimized route
        }

        return bestRoute;
    }


    /**
     * Uses the Ackley function to determine the perturbation strategy.
     *
     * @param route The current route.
     * @param iteration The current iteration number.
     * @return A perturbed route.
     */
    private static List<Integer> perturbRoute(List<Integer> route, int iteration) {
        double ackleyValue = calculateAckleyValue(iteration);
        int swapsToPerform = (int) (ackleyValue * 1.5 );
        System.out.println("SWAP " + swapsToPerform + " " + ackleyValue);
        for (int i = 0; i < swapsToPerform; i++) {
            swapRandomElements(route);
        }

        return route;
    }

    /**
     * Swaps two random elements in the route.
     *
     * @param route The route to be modified.
     */
    private static void swapRandomElements(List<Integer> route) {
        Random random = new Random();
        int index1 = random.nextInt(route.size());
        int index2 = random.nextInt(route.size());
        Collections.swap(route, index1, index2);
    }

    /**
     * Calculates the Ackley function value for a given iteration.
     *
     * @param iteration The iteration number.
     * @return The Ackley function value.
     */
    private static double calculateAckleyValue(int iteration) {
        // Parameters of the Ackley function
        double a = 20;
        double b = 0.2;
        double c = 2 * Math.PI;

        double sum1 = Math.pow(iteration, 2);
        double sum2 = Math.cos(c * iteration);

        return -a * Math.exp(-b * Math.sqrt(sum1)) - Math.exp(sum2) + a + Math.exp(1);
    }

    /**
     * Generates a random route from a start node for a given set of nodes.
     *
     * @param nodes The list of nodes to include in the route.
     * @return A random route starting from the start node.
     */
    public static List<Integer> randomHeuristic(List<Node> nodes) {
        Node startNode = NodeUtil.findStartNode(nodes);
        List<Node> unvisited = new ArrayList<>(nodes);
        List<Integer> path = new ArrayList<>();

        // Remove the start node from the unvisited list and add it to the path
        unvisited.remove(startNode);
        path.add(startNode.id);

        // Shuffle the remaining unvisited nodes
        Collections.shuffle(unvisited);

        // Add the shuffled nodes to the path
        for (Node node : unvisited) {
            path.add(node.id);
        }

        return path;
    }
    /**
     * Calculates the strength of the perturbation based on the current state of the search.
     *
     * @param iteration The current iteration number.
     * @param lastImprovementIteration The iteration number of the last improvement.
     * @return The strength of the perturbation.
     */
    private static int calculatePerturbationStrength(int iteration, int lastImprovementIteration) {
        int elapsedIterations = iteration - lastImprovementIteration;
        // Example logic: increase perturbation strength if no improvements have been made for a while
        if (elapsedIterations > 50) {
            return 3; // Strong perturbation
        } else if (elapsedIterations > 20) {
            return 2; // Medium perturbation
        } else {
            return 1; // Mild perturbation
        }
    }

    /**
     * Applies the GRASP methodology to the VRP.
     *
     * @param nodes                    The list of all nodes.
     * @param maxIterations            The number of iterations for the GRASP algorithm.
     * @param maxIterWithoutImprovement The maximum number of iterations without improvement before stopping.
     * @return An improved route.
     */
    public static List<Integer> grasp(List<Node> nodes, int maxIterations, int maxIterWithoutImprovement) {
        List<Integer> bestRoute = null;
        double bestCost = Double.MAX_VALUE;
        Node depot = NodeUtil.findStartNode(nodes); //NodeUtil.findNodeById(101, nodes);

        int lastImprovementIteration = 0; // Initialize the last improvement iteration

        for (int i = 0; i < maxIterations; i++) {
            List<Integer> initialSolution = greedyRandomizedConstruction(nodes, depot.id, 0.2);
            List<Integer> localOptimum = applyTwoOpt(initialSolution, nodes);
            double localOptimumCost = NodeUtil.totalDistance(localOptimum, nodes);

            if (localOptimumCost < bestCost) {
                bestRoute = new ArrayList<>(localOptimum);
                bestCost = localOptimumCost;
                lastImprovementIteration = i; // Update the last improvement iteration
            }

            // Stop if no improvement is seen for maxIterWithoutImprovement iterations
            if (i - lastImprovementIteration >= maxIterWithoutImprovement) {
                System.out.println("Stopping early due to no improvement.");
                break;
            }

            // Print the progress as a percentage
            double progress = (double) i / maxIterations * 100;
            System.out.printf("Iteration %d of %d (%.2f%% complete)  DISTANCE %f\n", i + 1, maxIterations, progress, bestCost);
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
        int depotId = NodeUtil.findStartNode(nodes).id;
        for (int i = 0; i < solutions.size(); i++) {

            solutions.get(i).remove(solutions.get(i).size() - 1);
            solutions.set(i, NodeUtil.reorderListWithDepotFirst(applyTwoOpt(solutions.get(i), nodes), nodes, 0));
            solutions.get(i).add(depotId);
        }
        return solutions;
    }


}
