import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class NumoebaWorld extends HashSet {
    int clock;

    public synchronized void clear() {
	super.clear();
	clock = 0;
    }

    public synchronized void step() {
	clock++;
	Set s = new HashSet(this);
	for (Iterator i = s.iterator(); i.hasNext();) {
	    Numoeba nn = (Numoeba)i.next();
	    nn.step();
	}
    }

    public synchronized int size() {
	int s = 0;
	for (Iterator i = iterator(); i.hasNext();) {
	    Numoeba nn = (Numoeba)i.next();
	    if (!nn.isDead()) s++;
	}
	return s;
    }
}
