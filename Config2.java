import java.util.Iterator;

public class Config2 extends Config {
    NumoebaWorld numoebaPool;
    Numoeba genom;

    public Config2(long i, Numoeba n) {
	super(i, n);
	numoebaPool = ((Numoeba3)n).subworld;
	genom = new Numoeba2(i, numoebaPool);
    }

    public Config2(long i, Numoeba n, NumoebaWorld nw) {
	super(i, n);
	numoebaPool = nw;
	genom = new Numoeba2(i, numoebaPool);
    }

    void takeOver(Config c) {
	genom.getLeader().fill(1);
	super.takeOver(c);
	numoebaPool = ((Config2)c).numoebaPool;
	genom = ((Config2)c).genom;
    }

    long nextNumbosome() {
	genom.step();
	return genom.getLeader().getNumbosome();
    }

    Config spawn(long i, Numoeba n) {
	if (Math.random() > (1.0  - genom.size() * 0.2))
	    return null;
	return new Config2(i, n);
    }

    void fill(long n) {
	numbosome = n;
	genom.getLeader().fill(n);
	for (Iterator i = subtree.iterator(); i.hasNext();) {
	    Config2 c = (Config2)i.next();
	    c.fill(n);
	}
    }

    public String toString() {
	String s = super.toString();
	return "[Config2:" + s + "]";
    }
}
