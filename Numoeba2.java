import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class Numoeba2 extends Numoeba {

    public Numoeba2(Config c, NumoebaWorld nw) {
	super(c, nw);
    }

    public Numoeba2(long seed, NumoebaWorld nw) {
	this(new Config(seed, null), nw);
	leader.nn = this;
    }

    public synchronized void step() {
	if (leader == null) return;
	generation++;
	max = 0;
	newLeader = null;
	maxElements.clear();
	sweepZombie(leader);
	if (reconfig() != null) {
	    if (newLeader != null) {
		newLeader.spawn1();
		changeLeader();
	    }
	}
    }

    Config sweepZombie(Config c) {
	if (c.numbosome == 1) {
	    if (c.equals(c.nn.leader)) return null;
	    if (c.subtree.size() == 0) return null;
	    if (c.subtree.size() == 1) {
		c.takeOver(c.subconfig());
		return sweepZombie(c);
	    }

	    if (Parameter.multiMode) {
		for (Iterator i = c.subtree.iterator(); i.hasNext();) {
		    Numoeba newNn = new Numoeba((Config)i.next(), world);
		    newNn.reorganize();
		}
	    }

	    return null;
	}

	Set s = new HashSet();
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    Config sc = sweepZombie((Config)i.next());
	    if (sc != null) s.add(sc);
	}
	c.subtree = s;
	return c;
    }

    protected Config reconfig(Config c) {
	if (c == null) return null;
	long x = c.nextNumbosome();
	if (x > max) {
	    max = x;
	    newLeader = c;
	    maxElements.clear();
	    maxElements.add(c);
	}
	else if (x == max) {
	    maxElements.add(c);
	    newLeader = null;
	}
	if (c.subtree.isEmpty() && (x > c.numbosome)) {
	    c.numbosome = x;
	    c.spawn0();
	    return c;
	}
	if (x == 1) {
	    c.numbosome = 1;
	    if (c.equals(c.nn.leader)) return null;
	    if (c.subtree.size() != 1) {
		if (!Parameter.multiMode) c.fill(1);
		return c;
	    }
	}

	c.numbosome = x;
	Set s = new HashSet();
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    Config sc = reconfig((Config)i.next());
	    if (sc != null) s.add(sc);
	}
	c.subtree = s;
	return c;
    }

    protected int nodeCount(Config c) {
	if (c == null) return 0;
	int count = 1; // this node
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    Config cc = (Config)i.next();
	    if (!cc.isDead())
		count += nodeCount(cc);
	}
	return count;
    }


}
