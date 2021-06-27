
public class Edge {
    Node from;
    Node to;
    double len;
    
    public int hashCode() {
	return (from.hashCode() + to.hashCode());
    }

    public boolean equals(Object o) {
        if (!(o instanceof Edge)) return false;
        Edge e = (Edge)o;
        return from.equals(e.from) && to.equals(e.to);
    }
}
