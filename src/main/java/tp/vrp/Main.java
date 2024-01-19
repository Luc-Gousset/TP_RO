package tp.vrp;

import tp.vrp.Data.Node;
import tp.vrp.Data.Request;
import tp.vrp.Data.Vehicule;
import tp.vrp.parser.XMLParser;
import tp.vrp.Data.Edge;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {


    public static void main(String[] args) {


        XMLParser parser = new XMLParser();
        parser.parseXMLFile("JDD01.xml");
        List<Node> nodes = parser.getNodeList();
        List<Request> requests = parser.getRequestList();
        List<Vehicule> vehicules = parser.getVehicleList();


        List<Integer> dumbPath = RouteOptimizationAlgorithms.dumbHeuristic(nodes);

        double minDistance = Double.MAX_VALUE;
        List<Integer> bestSolution = new ArrayList<>();
        List<List<Integer>> bestSolutionsForVehicles = new ArrayList<>();
        long startTime = System.nanoTime();

        for (int i = 0; i < 20; i++) {
            List<Integer> ilsResult = RouteOptimizationAlgorithms.iteratedLocalSearchWithTwoOpt(
                    RouteOptimizationAlgorithms.randomHeuristic(nodes), nodes, 150, 10);
            double currentDistance = NodeUtil.totalDistance(ilsResult, nodes);
            System.out.println("ILS distance sequence " + i + " " + currentDistance);

            List<Integer> shortestPath = NodeUtil.reorderListWithDepotFirst(ilsResult, nodes, 0);

            List<List<Integer>> solutions = computeSolFromSegment(shortestPath, nodes, requests, vehicules);
            solutions = RouteOptimizationAlgorithms.apply2OptOnSol(solutions, nodes);

            double routeDIs = NodeUtil.getRouteRes(solutions, nodes);
            // Check if the current distance is less than the minimum distance found so far
            if (routeDIs < minDistance) {
                minDistance = routeDIs;
                bestSolution = new ArrayList<>(shortestPath);
                bestSolutionsForVehicles = new ArrayList<>(solutions); // Store the best solutions for each vehicle
            }

            NodeUtil.printRouteResults(solutions, nodes);
            //TourPlotter.plotTours(solutions, nodes);
            //TourPlotter.plotSequence(shortestPath, nodes);
        }
        long endTime = System.nanoTime();
        // Calculate elapsed time in milliseconds
        long duration = (endTime - startTime) / 1_000_000_000; // Convert to milliseconds

        // Print or process the best overall solution after all iterations
        System.out.println("Best Overall Distance: " + minDistance);
        NodeUtil.printRouteResults(bestSolutionsForVehicles, nodes);
        TourPlotter.plotTours(bestSolutionsForVehicles, nodes);
        TourPlotter.plotSequence(bestSolution, nodes);
        System.out.println("Execution Time: " + duration + " seconds");


    }







    private static List<Integer> ford(int start, int end, List<Edge> edges, int numVertices) {
        double[] distances = new double[numVertices];
        int[] predecessors = new int[numVertices];
        Arrays.fill(distances, Double.MAX_VALUE);
        distances[start] = 0;
        Arrays.fill(predecessors, -1);

        // Relax edges repeatedly
        for (int i = 1; i < numVertices - 1; i++) {
            for (Edge edge : edges) {
                if (distances[edge.source] + edge.weight < distances[edge.destination]) {
                    distances[edge.destination] = distances[edge.source] + edge.weight;
                    predecessors[edge.destination] = edge.source;
                }
            }
        }

        // Reconstruct path from start to end
        List<Integer> path = new ArrayList<>();
        int current = end;
        while (current != -1 && current != start) {
            path.add(0, current); // Add Node corresponding to index 'current'
            current = predecessors[current];
        }
        if (current != -1) {
            path.add(0, start);
        }

        return path;
    }








    public static List<List<Integer>> computeSolFromSegment(List<Integer> shortestPath, List<Node> nodes, List<Request> requests, List<Vehicule> vehicules){
        List<Edge> edges = new ArrayList<>();
        int depotId = NodeUtil.findStartNode(nodes).id;
        for (int i = 0; i < shortestPath.size(); i++) {
            double current_load = 0;
            double current_distance = 0;

            for (int y = i + 1; y < shortestPath.size(); y++) {
                Node previous_node = NodeUtil.findNodeById(shortestPath.get(y - 1), nodes);
                Node current_node = NodeUtil.findNodeById(shortestPath.get(y), nodes);
                current_distance += Node.GetDistance(previous_node, current_node);


                Request current_request = null;
                if (requests.stream().anyMatch(request -> request.getNode() == current_node.id)) {
                    current_request = requests.stream().filter(request -> request.getNode() == current_node.id).findFirst().get();
                    if (current_request.getQuantity() + current_load <= vehicules.get(0).getCapacityInitial()) {
                        current_load += current_request.getQuantity();

                        edges.add(new Edge(shortestPath.get(i), current_node.id, current_distance + Node.GetDistance(current_node, NodeUtil.findStartNode(nodes))));

                    } else
                        break;
                } else
                    break;

            }


        }

        //Ford algo:
        List<Integer> edgesList = ford(shortestPath.getFirst(), shortestPath.getLast(), edges, shortestPath.size() + 1);

        int currentNodeStopId = 1;
        List<List<Integer>> solutions = new ArrayList<>();
        List<Integer> sol = new ArrayList<>();
        sol.add(depotId);
        for (int i = 1; i < shortestPath.size(); i++) {
            if (Objects.equals(shortestPath.get(i), edgesList.get(currentNodeStopId))) {
                sol.add(shortestPath.get(i));
                sol.add(depotId);
                //sol.requestArrayList = (ArrayList<Request>) requests;
                solutions.add(sol);
                sol = new ArrayList<>();
                sol.add(depotId);
                if (currentNodeStopId < edgesList.size() - 1)
                    currentNodeStopId++;
            } else {
                sol.add(shortestPath.get(i));
            }
        }
        return solutions;
    }



}