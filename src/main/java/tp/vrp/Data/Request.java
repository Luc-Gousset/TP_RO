package tp.vrp.Data;


/**
 * La classe Request représente une requête dans le cadre d'un problème de routage de véhicules.
 * Elle contient des informations sur la quantité demandée, le nœud associé à la requête,
 * ainsi qu'un identifiant unique pour chaque requête.
 */
public class Request {
    private double quantity ;
    private int node ;
    private int id;

    public Request() {
    }

    public Request (double quantity, int node , int id ){
        setId(id);
        setNode(node);
        setQuantity(quantity);
    }
    /**
     * Récupère la quantité demandée par la requête.
     *
     * @return La quantité demandée.
     */
    public double getQuantity() {
        return quantity;
    }
    /**
     * Définit la quantité demandée par la requête.
     *
     * @param quantity La quantité à définir.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    /**
     * Définit l'identifiant de la requête.
     *
     * @param id L'identifiant à définir.
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Récupère l'identifiant de la requête.
     *
     * @return L'identifiant de la requête.
     */
    public int getId() {
        return id;
    }
    /**
     * Récupère le nœud associé à la requête.
     *
     * @return Le nœud associé.
     */
    public int getNode() {
        return node;
    }
    /**
     * Définit le nœud associé à la requête.
     *
     * @param node Le nœud à définir.
     */
    public void setNode(int node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return "Request{" +
                "quantity=" + quantity +
                ", node=" + node +
                ", id=" + id +
                '}';
    }
}
