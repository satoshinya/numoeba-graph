import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Config implements Comparable {
    Numoeba nn;
    long numbosome;
    Set subtree;
    
    public Config(long i, Numoeba n) {
	nn = n;
	numbosome = i;
	subtree = new HashSet();
    }

    public int compareTo(Object o) {
	int i = compareTo0(o);
	return (i == 0 ? objectId().compareTo(((Config)o).objectId()) : i);
    }

    public int compareTo0(Object o) {
	if (equals(o)) return 0;
	Config c = (Config)o;
	long d = numbosome - c.numbosome;
	if (d < 0) return -1;
	if (d > 0) return 1;
	
	Set s1 = new TreeSet(subtree);
	Set s2 = new TreeSet(c.subtree);
	Iterator i1 = s1.iterator();
	Iterator i2 = s2.iterator();

	while (i1.hasNext() && i2.hasNext()) {
	    Config c1 = (Config)i1.next();
	    Config c2 = (Config)i2.next();
	    int j = c1.compareTo0(c2);
	    if (j != 0) return j;
	}
	return subtree.size() - c.subtree.size();
    }

    boolean isLeader() {
	return equals(nn.leader);
    }

    boolean isMaxElement() {
	return nn.maxElements.contains(this);
    }

    boolean isDead() {
	return (numbosome == 1);
    }

    boolean isDying() {
	return false;
    }

    void fill(long n) {
	numbosome = n;
	for (Iterator i = subtree.iterator(); i.hasNext();) {
	    Config c = (Config)i.next();
	    c.fill(n);
	}
    }

    int getLife() {
	long s = numbosome;
	int i = 1;
	while ((s = nextNumbosomeOf(s)) != 1) i++;
	return i;
    }

    long getNumbosome() {
	return numbosome;
    }

    void setNumbosome(long n) {
	numbosome = n;
    }

    void takeOver(Config c) {
	numbosome = c.numbosome;
	subtree = c.subtree;
    }

    long nextNumbosome() {
	return nextNumbosomeOf(numbosome);
    }

    long nextNumbosomeOf(long n) {
	long i = n * Parameter.collatzCoefficient + Parameter.collatzConstant;
	while (i % 2 == 0) i /= 2;
	if (Parameter.numbosomeBound != 0 && i > Parameter.numbosomeBound)
	    i = i % Parameter.numbosomeBound;
	return i;
    }

    Config subconfig() {
	return (Config)subtree.iterator().next();
    }

    Config spawn(long i, Numoeba n) {
    	return new Config(i, n);
    }

    void spawn0() {
	long i = (numbosome + Parameter.spawnLeafConstant) / 2;
	Config c = spawn(((i % 2)==0) ? (i + 1) : i, nn);
	if (c != null) subtree.add(c);
    }

    void spawn1() {
	long i = (numbosome + Parameter.spawnLeaderConstant) / 2;
	Config c = spawn(((i % 2)==0) ? (i + 1) : i, nn);
	if (c != null) subtree.add(c);
    }

    public String objectId() {
	return super.toString();
    }

    public String toString() {
	StringBuffer s = new StringBuffer();
	s.append("(").append(numbosome);
	if (!subtree.isEmpty()) {
	    s.append(" ");
	    for (Iterator i = subtree.iterator(); i.hasNext();) {
		s.append(i.next());
	    }
	}
	s.append(")");
	return s.toString();
    }

    public String toUniqueString() {
	StringBuffer s = new StringBuffer();
	s.append("(").append(numbosome);
	if (!subtree.isEmpty()) {
	    s.append(" ");
	    Set ss = new TreeSet(subtree); // sort
	    for (Iterator i = ss.iterator(); i.hasNext();) {
		Config c = (Config)i.next();
		s.append(c.toUniqueString());
	    }
	}
	s.append(")");
	return s.toString();
    }

    List searchLeader(Config leader) {
	if (equals(leader)) {
	    List l = new ArrayList();
	    l.add(this);
	    return l;
	}	
	for (Iterator i = subtree.iterator(); i.hasNext();) {
	    Config c = (Config)i.next();
	    List l = c.searchLeader(leader);
	    if (l != null) {
		l.add(this);
		return l;
	    }
	}
	return null;
    }
}
