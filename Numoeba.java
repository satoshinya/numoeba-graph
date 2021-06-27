import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Numoeba {
    NumoebaWorld world; // where this object belongs
    long generation;
    long max;
    Config leader;
    Config newLeader;
    Set maxElements;
        
    public Numoeba(Config c, NumoebaWorld nw) {
	world = nw;
	nw.add(this);
	generation = 0;
	leader = c;
	newLeader = null;
	maxElements = new HashSet();
	c.nn = this;	
    }

    public Numoeba(long seed, NumoebaWorld nw) {
	this(new Config(seed, null), nw);
	leader.nn = this;
    }

    public String toString() {
	StringBuffer s = new StringBuffer();
	s.append("[Leader=").append(leader).append("]");
	return s.toString();
    }

    public static void main(String[] args) {
	NumoebaWorld nw = new NumoebaWorld();
	new Numoeba(Integer.parseInt(args[0]), nw);
	int clock = 0;
	String s = null;
	while(clock < 1000 && nw.size() != 0) {
	    nw.step();
	    for (Iterator i = nw.iterator(); i.hasNext();) {
		Numoeba nn = (Numoeba)i.next();
		s = nn.leader.toUniqueString();
	    }
	    clock++;
	}
	System.out.println(clock + " " + s);
    }

    public void start(int clockMax) {
	while(clockMax-- > 0 && leader != null) {
	    step();
	}
    }

    public synchronized void destruct() {
	leader.setNumbosome(1);
    }

    public synchronized boolean isDead(){
	return leader.getNumbosome() == 1;
    }

    public synchronized Config getLeader(){
	return leader;
    }

    public synchronized String getLeaderId(){
	return leader.objectId();
    }

    public synchronized long getGeneration(){
	return generation;
    }
    
    public synchronized void step() {
	if (leader == null) return;
	generation++;
	max = 0;
	newLeader = null;
	maxElements.clear();
	if (reconfig() != null) {
	    if (newLeader != null) {
		newLeader.spawn1();
		changeLeader();
	    }
	}
    }

    public synchronized Set getMaxElements() {
	return maxElements;
    }

    public synchronized Config changeLeader() {
	List l = leader.searchLeader(newLeader);
	Config c1 = null;
	for (Iterator i = l.iterator(); i.hasNext();){
	    Config c2 = (Config)i.next();
	    if (c1 == null) {
		c1 = c2;
		continue;
	    }
	    c1.subtree.add(c2);
	    c2.subtree.remove(c1);
	    c1 = c2;
	}
	leader = newLeader;
	return leader;
    }

    public synchronized void reorganize() {	
	reorganize(leader);
    }

    public synchronized void reorganize(Config c) {
	// detach this from the previous Numoeba and attach it to new one.
	c.nn = this;
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
            reorganize((Config)i.next());
	}
    }

    public synchronized Config reconfig() {
	return reconfig(leader);
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
	if (c.subtree.isEmpty() && (x > c.getNumbosome())) {
	    c.setNumbosome(x);
	    c.spawn0();
	    return c;
	}
	if (x == 1) {
	    c.setNumbosome(x); // mark the cell as dead, for future benefit
	    if (c.equals(c.nn.leader)) return null;
	    if (c.subtree.size() == 0) return null;
	    if (c.subtree.size() == 1) {
		c.takeOver(c.subconfig());
		return reconfig(c);
	    }
	    if (Parameter.multiMode) {
		for (Iterator i = c.subtree.iterator(); i.hasNext();) {
		    Numoeba newNn = new Numoeba((Config)i.next(), world);
		    newNn.reorganize(); // not implemeted yet.
		}
	    }
	    return null; // this cell is dead anyway.
	}
	c.setNumbosome(x);
	Set s = new HashSet();
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    Config sc = reconfig((Config)i.next());
	    if (sc != null) s.add(sc);
	}
	c.subtree = s;
	return c;
    }

    public class Link {
	Config from;
	Config to;
	public Link(Config f, Config t) {
	    from = f;
	    to = t;
	}
    }

    /**
     *  Returns the number of cells.
     */
    public synchronized Set getLinks() {
	Set linkSet = new HashSet();
	getLinks(leader, linkSet);
	return linkSet;
    }

    private void getLinks(Config c, Set lset) {
	if (c == null) return;
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    Config child = (Config)i.next();
	    lset.add(new Link(c, child));
	    getLinks(child, lset);
	}
    }

    public int size() {
	return nodeCount(leader);
    }

    protected int nodeCount(Config c) {
	if (c == null) return 0;
	int count = 1; // this node
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    count += nodeCount((Config)i.next());
	}
	return count;
    }

    protected long sum(Config c) {
	if (c == null) return 0;
	long s = c.numbosome;
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    s += sum((Config)i.next());
	}
	return s;
    }

    protected long sum2(Config c) {
	if (c == null) return 0;
	long s = 0;
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    s += c.numbosome * sum((Config)i.next());
	}
	return s;
    }

    public long getMax() {
	return getMax(leader);
    }

    public long getMax(Config c) {
	if (c == null) return 0;
	long m = c.getNumbosome();
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    long cm = getMax((Config)i.next());
	    if (cm > m) m = cm;
	}
	return m;
    }

    public long getMin() {
	return getMin(leader);
    }

    public long getMin(Config c) {
	if (c == null) return 0;
	long m = c.getNumbosome();
	for (Iterator i = c.subtree.iterator(); i.hasNext();) {
	    long cm = getMax((Config)i.next());
	    if (cm < m) m = cm;
	}
	return m;
    }

}
