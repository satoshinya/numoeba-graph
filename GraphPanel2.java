import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Iterator;

public class GraphPanel2 extends GraphPanel {

    public GraphPanel2(NumoebaGraph f) {
	super(f);
    }

    protected Numoeba createNumoeba(int i, NumoebaWorld w) {
	return new Numoeba2(i, w);
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

	if (e2.from.s != 0 || e2.to.s != 0) e2.len = 0;
	else e2.len = linkLen;

	return e2;
    }

    void attributeNode(Node n, Config c) {
	n.fg = normalFgColor;
	n.bg = normalBgColor;

	if (c.isDead()) {
	    n.fg = passiveFgColor;
	    if (c.isLeader()) n.bg = deadColor;
	    else n.bg = passiveBgColor;
	}
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

	if (c.isDead() && !c.isLeader()) n.s = 1;
	else n.s = 0;

	if (c instanceof Config2) {
	    n.bg = Color.white;
	}
    }

    public void paintNode(Graphics g, Node n, FontMetrics fm) {
	int x = (int)n.x;
	int y = (int)n.y;
	g.setColor((n == pick) ? selectColor : n.bg);
	int w = fm.stringWidth(n.lbl) + 10;
	int h = fm.getHeight() + 4;
	if (n.s != 0) {
	    double hh = (double)h - (double)n.s * 1.5;
	    h = (hh < 1.0 ? 1 : (int) hh);
	    double ww = (double)w - (double)n.s * 1.5;
	    w = (ww < 1.0 ? 1 : (int) ww);
	    n.s++;
	}
	g.fillRoundRect(x - w/2, y - h / 2, w, h, rectArcRadius, rectArcRadius);
	g.setColor(n.border);
	g.drawRoundRect(x - w/2, y - h / 2, w-1, h-1, rectArcRadius, rectArcRadius);
	g.setColor(n.fg);
	if (n.s == 0) 
	    g.drawString(n.lbl, x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
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

	    /*
	    e.len -= ((double)e.from.s * 1.5 + (double)e.to.s * 1.5);
	    if (e.len < 1.0) e.len = 1.0;
	    */

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

}
