package tp.vrp.Data;

public class Edge {
    public int source;
    public int destination;
    public double weight;

    public Edge(int s, int d, double w) {
        source = s;
        destination = d;
        weight = w;
    }
}

