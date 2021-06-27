import java.awt.Color;

public class Node {
    double x;
    double y;
    double dx;
    double dy;

    int s = 0;

    String lbl;
    String id;
    Color fg;
    Color bg;
    Color border = Color.black;

    public int hashCode() {
	return lbl.hashCode();
    }

    public boolean equals(Object o) {
	if (!(o instanceof Node)) return false;
	Node n = (Node)o;
	return id.equals(n.id);
    }
}
