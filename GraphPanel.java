import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class GraphPanel extends Panel implements Runnable, MouseListener, MouseMotionListener {
    NumoebaGraph frame;
    Map nodes;
    Map edges;
    Thread relaxer;
    final int linkLen = 30;
    int updateInterval;
    int updateRate;     // #relax/#step

    NumoebaWorld nw;
    boolean running;

    GraphPanel(NumoebaGraph f) {
	frame = f;
	nodes = new HashMap();
	edges = new HashMap();
	updateInterval = 100;
	updateRate = 10;
	addMouseListener(this);

	nw = new NumoebaWorld();
	running = false;
    }

    synchronized void setUpdateInterval(int i) {
	updateInterval = i;
    }

    synchronized void setUpdateRate(int i) {
	updateRate = i;
    }

    synchronized void setRunning(boolean flag) {
	running = flag;
    }

    synchronized boolean isRunning() {
	return running;
    }

    synchronized Node findNode(String id) {
	return (Node)nodes.get(id);
    }

    synchronized Node findNode(String id, String lbl) {
	Node n = (Node)nodes.get(id);
	if (n == null) {
	    n = addNode(id, lbl);
	}
	else {
	    n.lbl = lbl; // update.
	}
	return n;
    }

    synchronized Node findNode(String id, String lbl, int x, int y) {
	Node n = (Node)nodes.get(id);
	if (n == null) {
	    n = addNode(id, lbl);
	}
	else {
	    n.lbl = lbl; // update.
	}
	n.x = x;
	n.y = y;
	return n;
    }

    synchronized Node findNode(String id, String lbl, Node node) {
	Node n = (Node)nodes.get(id);
	if (n == null) {
	    n = addNodeNearTo(id, lbl, node);
	}
	else {
	    n.lbl = lbl; // update.
	}
	return n;
    }

    synchronized Edge findEdge(String i1, String l1, String i2, String l2) {
	Node n1 = new Node(); n1.id = i1; n1.lbl = l1;
	Node n2 = new Node(); n2.id = i2; n2.lbl = l2;
	Edge e1 = new Edge();
	e1.from = n1;
	e1.to = n2;
	Edge e2 = (Edge)edges.get(e1);
	if (e2 == null) {
	    e2 = addEdge(i1, l1, i2, l2, linkLen);
	}
	return e2;
    }

    synchronized Node addNode(String id, String lbl) {
	Node n = new Node();
	n.x = 10 + 380*Math.random();
	n.y = 10 + 380*Math.random();
	n.lbl = lbl;
	n.id = id;
	nodes.put(id, n);
	return n;
    }

    synchronized Node addNodeNearTo(String id, String lbl, Node node) {
	Node n = new Node();
	n.x = node.x + 20*Math.random() - 10;
	n.y = node.y + 20*Math.random() - 10;
	n.lbl = lbl;
	n.id = id;
	nodes.put(id, n);
	return n;
    }

    synchronized Edge addEdge(String i1, String l1, String i2, String l2, int len) {
	Edge e = new Edge();
	e.from = findNode(i1, l1);
	e.to = findNode(i2, l2, e.from);
	e.len = len;
	edges.put(e, e);
	return e;
    }

    final Color normalFgColor = Color.black;
    final Color normalBgColor = new Color(153, 153, 255);
    final Color deadColor = new Color(200, 0, 0);
    final Color maxElementColor = new Color(51, 255, 102);
    final Color medColor2 = new Color(180, 230, 180);
    final Color higColor = new Color(250, 250, 0);
    final Color leaderColor = new Color(255, 0, 0);

    final Color passiveFgColor = new Color(88, 88, 88);
    final Color passiveBgColor = new Color(204, 204, 255);

    void attributeNode(Node n, Config c) {
	if (c.isDying()) n.fg = passiveFgColor;
	else n.fg = Color.black;

	if (c.isDead()) n.bg = deadColor;
	else if (c.isLeader()) n.bg = leaderColor;
	else if (c.isMaxElement()) {
	    if (c.isDying())
		n.bg = medColor2;
	    else 
		n.bg = maxElementColor;
	}
	else if (c.isDying())
	    n.bg = passiveBgColor;
	else 
	    n.bg = normalBgColor;	
    }

    synchronized void updateNumoebas() {
	Map nNodes = new HashMap();
	Map nEdges = new HashMap();
	nw.step();
	for (Iterator in = nw.iterator(); in.hasNext();) {
	    Numoeba nn = (Numoeba)in.next();
	    Set ls = nn.getLinks();
	    for (Iterator i = ls.iterator(); i.hasNext();) {
		Numoeba.Link l = (Numoeba.Link)i.next();
		Edge edge = findEdge(l.from.objectId(),
				     Long.toString(l.from.numbosome),
				     l.to.objectId(),
				     Long.toString(l.to.numbosome));
		nNodes.put(l.from.objectId(), edge.from);
		nNodes.put(l.to.objectId(), edge.to);

		attributeNode(edge.from, l.from);
		attributeNode(edge.to, l.to);

		nEdges.put(edge, edge);
	    }
	}
	nodes = nNodes;
	edges = nEdges;

	frame.printStatus(nw.clock, nNodes.size());
    }

    protected Numoeba createNumoeba(int i, NumoebaWorld w) {
	return new Numoeba(i, w);
    }

    synchronized Numoeba putSeed(int i) {
	Numoeba nn = createNumoeba(i, nw);
	Dimension d = getSize();
	Node n = findNode(nn.getLeader().objectId(),
			  Long.toString(nn.getLeader().numbosome),
			  d.width/2, d.height/2);
	attributeNode(n, nn.getLeader());
	return nn;
    }

    synchronized void clear() {
	nw.clear();
	clearScreen();
    }

    synchronized void clearScreen() {
	nodes.clear();
	edges.clear();
	repaint();
    }

    synchronized boolean nothingToDraw() {
	return nodes.isEmpty();
    }

    public void run() {
	while (true) {
	    if (nothingToDraw()) {
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		    break;
		}
		continue;
	    }
	    for (int i = 0; i < updateRate; i++) {
		long time = System.currentTimeMillis();
		relax();
		time = updateInterval + time - System.currentTimeMillis();
		if (time > 0) {
		    try {
			Thread.sleep(time);
		    } catch (InterruptedException e) {
			break;
		    }
		}
	    }

	    if (nw.size() != 0 && running) {
		updateNumoebas();
	    }
	    /*
	     * nw.size() == 0  =>  no numoebas alive (there may be zombies)
	     * nw.isEmpty()    =>  nothing in the world
	     *
	     */
	    if (nw.size() == 0) {
		if (isRunning() && Parameter.autoRepeat) {
		    
		    /*
		     * aftereffect of the current numoeba.
		     */
                    final int aftereffect = 50;
                    for (int i = 0; i < aftereffect; i++) {
                        relax();
                        try {
                            Thread.sleep(updateInterval);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }

		    clear(); // sweep zombies out & clear screen
		    int seed = frame.getSeedValue();
		    if (seed == 0) seed = 1;
		    seed = (seed + 2) % 10000; // ad hoc.
		    frame.putSeed(seed);
		}
		else if (nothingToDraw()) {
		    clearScreen();
		}
	    }
	}
    }

    static final double borderWidth = 20.0;

    synchronized void relax() {
	for (Iterator i = edges.keySet().iterator(); i.hasNext();) {
	    Edge e = (Edge)i.next();
	    double vx = e.to.x - e.from.x;
	    double vy = e.to.y - e.from.y;
	    double len = Math.sqrt(vx * vx + vy * vy);
            len = (len == 0) ? .0001 : len;
	    double f = (e.len - len) / (len * 3);
	    double dx = f * vx;
	    double dy = f * vy;

	    e.to.dx += dx;
	    e.to.dy += dy;
	    e.from.dx += -dx;
	    e.from.dy += -dy;
	}

	for (Iterator i = nodes.values().iterator(); i.hasNext();) {
	    Node n1 = (Node)i.next();
	    double dx = 0;
	    double dy = 0;

	    for (Iterator j = nodes.values().iterator(); j.hasNext();) {
		Node n2 = (Node)j.next();
		if (n1.equals(n2)) continue;
		double vx = n1.x - n2.x;
		double vy = n1.y - n2.y;
		double len = vx * vx + vy * vy;
		if (len == 0) {
		    dx += Math.random();
		    dy += Math.random();
		} else if (len < 100*100) {
		    dx += vx / len;
		    dy += vy / len;
		}
	    }
	    double dlen = dx * dx + dy * dy;
	    if (dlen > 0) {
		dlen = Math.sqrt(dlen) / 2;
		n1.dx += dx / dlen;
		n1.dy += dy / dlen;
	    }
	}

	Dimension d = getSize();
	double cx = d.width/2;
	double cy = d.height/2;
	double xmax = 0;
	double ymax = 0;
	double xmin = d.width;
	double ymin = d.height;

	final double dshift = 1;

	for (Iterator i = nodes.values().iterator(); i.hasNext();) {
	    Node n = (Node)i.next();

	    n.x += Math.max(-5, Math.min(5, n.dx));
	    n.y += Math.max(-5, Math.min(5, n.dy));

            if (n.x < borderWidth) {
                n.x = borderWidth;
            } else if (n.x > d.width - borderWidth) {
                n.x = d.width - borderWidth;
            }
            if (n.y < borderWidth) {
                n.y = borderWidth;
            } else if (n.y > d.height - borderWidth) {
                n.y = d.height - borderWidth;
            }
	    n.dx /= 2;
	    n.dy /= 2;
	    
	    if (n.x > xmax) xmax = n.x;
	    if (n.y > ymax) ymax = n.y;
	    if (n.x < xmin) xmin = n.x;
	    if (n.y < ymin) ymin = n.y;
	}
	
	double sx = ((xmax + xmin)/2 > cx) ? -dshift : dshift;
	double sy = ((ymax + ymin)/2 > cy) ? -dshift : dshift;

	for (Iterator i = nodes.values().iterator(); i.hasNext();) {
            Node n = (Node)i.next();
	    n.x += sx;
	    n.y += sy;
	}
	repaint();
    }

    Node pick;
    Image offscreen;
    Dimension offscreensize;
    Graphics offgraphics;

    final Color selectColor = Color.pink;
    final Color edgeColor = Color.black;
    final Color stressColor = Color.darkGray;
    final Color arcColor1 = Color.black;
    final Color arcColor2 = Color.pink;
    final Color arcColor3 = Color.red;

    final int rectArcRadius = 5;

    public void paintNode(Graphics g, Node n, FontMetrics fm) {
	int x = (int)n.x;
	int y = (int)n.y;
	g.setColor((n == pick) ? selectColor : n.bg);
	int w = fm.stringWidth(n.lbl) + 10;
	int h = fm.getHeight() + 4;
	g.fillRoundRect(x - w/2, y - h / 2, w, h, rectArcRadius, rectArcRadius);
	g.setColor(n.border);
	g.drawRoundRect(x - w/2, y - h / 2, w-1, h-1, rectArcRadius, rectArcRadius);
	g.setColor(n.fg);
	g.drawString(n.lbl, x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
    }

    protected void prepareScreen() {
	Dimension d = getSize();
	if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
	    offscreen = createImage(d.width, d.height);
	    offscreensize = d;
	    offgraphics = offscreen.getGraphics();
	    offgraphics.setFont(getFont());
	}
	offgraphics.setColor(getBackground());
	offgraphics.fillRect(0, 0, d.width, d.height);
    }
    
    public synchronized void update(Graphics g) {
	prepareScreen();
	for (Iterator i = edges.keySet().iterator(); i.hasNext();) {
	    Edge e = (Edge)i.next();
	    int x1 = (int)e.from.x;
	    int y1 = (int)e.from.y;
	    int x2 = (int)e.to.x;
	    int y2 = (int)e.to.y;
	    int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) - e.len);

	    offgraphics.setColor(arcColor1);
	    offgraphics.drawLine(x1, y1, x2, y2);

	    int dx, dy;
	    if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
		dx = 0; dy = 1;
	    } else {
		dx = 1; dy = 0;
	    }
	    offgraphics.drawLine(x1 + dx, y1 + dy, x2 + dx, y2 + dy);
	}

	FontMetrics fm = offgraphics.getFontMetrics();
	for (Iterator i = nodes.values().iterator(); i.hasNext();) {
	    Node n = (Node)i.next();
	    paintNode(offgraphics, n, fm);
	}
	g.drawImage(offscreen, 0, 0, null);
    }

    //1.1 event handling
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        addMouseMotionListener(this);
	double bestdist = Double.MAX_VALUE;
	int x = e.getX();
	int y = e.getY();
	for (Iterator i = nodes.values().iterator(); i.hasNext();) {
	    Node n = (Node)i.next();
	    double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
	    if (dist < bestdist) {
		pick = n;
		bestdist = dist;
	    }
	}
	if (pick != null) {
	    pick.x = x;
	    pick.y = y;
	    repaint();
	}
	e.consume();
    }

    public void mouseReleased(MouseEvent e) {
        removeMouseMotionListener(this);
	if (pick != null) {
	    pick.x = e.getX();
	    pick.y = e.getY();
	    pick = null;
	    repaint();
	}
	e.consume();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	if (pick != null) {
	    pick.x = e.getX();
	    pick.y = e.getY();
	    repaint();
	}
	e.consume();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void start() {
	relaxer = new Thread(this);
	relaxer.start();
    }

    public void stop() {
	relaxer = null;
    }
}
