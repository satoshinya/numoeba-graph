import java.util.Iterator;

public class Numoeba3 extends Numoeba2 {
    NumoebaWorld subworld;

    public Numoeba3(Config c, NumoebaWorld nw, NumoebaWorld snw) {
	super(c, nw);
	subworld = snw;
    }

    public Numoeba3(long seed, NumoebaWorld nw, NumoebaWorld snw) {
	this(new Config2(seed, null, snw), nw, snw);
	leader.nn = this;
    }

    public synchronized void step() {
	super.step();
    }

    public synchronized void destruct() {
	super.destruct();
	cleanup((Config2)leader);
    }

    protected void cleanup(Config2 c) {
	if (c == null) return;
	c.genom.destruct();
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    Config2 cc = (Config2)i.next();
	    cleanup(cc);
	}
    }
}
