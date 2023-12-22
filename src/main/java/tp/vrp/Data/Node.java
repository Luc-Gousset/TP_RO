package tp.vrp.Data;

public class Node {

    private static final double R = 6370.7; // en km
    public int id;
    public double longitude;
    public double latitude;

    public int type;


    public Node(int id, double longitude, double latitude,int type) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.type =type;
    }

    public static double GetDistance(Node a, Node b){
        //R arccos (sin(φ1) sin(φ2) + cos(φ1) cos(φ2) cos(Ψ1 − Ψ2))
        //return R * Math.acos(Math.sin(a.latitude)*Math.sin(b.latitude)+Math.cos(a.latitude)*Math.cos(b.latitude)*Math.cos(a.longitude-b.longitude));
        return Math.sqrt(Math.pow(a.longitude-b.longitude, 2)+ Math.pow(a.latitude-b.latitude, 2));

    }

    public Node() {
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
