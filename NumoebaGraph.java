/**
 *  NumoebaGraph.java
 *
 *  Graphical Numoeba simulation by Shin-ya Sato <shin-ya.sato@acm.org>.
 *  Thanks to Graph.java for visualizing graph structures.
 */

/*
 * @(#)Graph.java	1.7 98/07/17
 *
 * Copyright (c) 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class FileMenu extends Menu implements ActionListener {
    public FileMenu() {
	super("File");
	MenuItem mi;
	add(mi = new MenuItem("Quit"));
	mi.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
	String item = e.getActionCommand();
	if (item.equals("Quit")) 
	    System.exit(0);
    }
}

class ConfigMenu extends Menu {

    public ConfigMenu() {
	super("Config");

	MenuItemGroup mig = new MenuItemGroup(this);
	mig.add("1+1-", (Parameter.spawnLeaderConstant == -1));
	mig.add("1+1+", (Parameter.spawnLeaderConstant == 1));
	mig.add("1-1-", (Parameter.spawnLeaderConstant == 1));
	mig.addItemListener(new ItemListener(){
	    public void itemStateChanged(ItemEvent e) {
		CheckboxMenuItem cim = (CheckboxMenuItem)e.getItemSelectable();
		if (cim.getLabel().equals("1+1-")) {
		    Parameter.spawnLeafConstant = 1;
		    Parameter.spawnLeaderConstant = -1;
		}
		else if (cim.getLabel().equals("1+1+")) {
		    Parameter.spawnLeafConstant = 1;
		    Parameter.spawnLeaderConstant = -1;
		}
		else if (cim.getLabel().equals("1-1-")) {
		    Parameter.spawnLeafConstant = -1;
		    Parameter.spawnLeaderConstant = -1;
		}
	    }
	});

	addSeparator();

	/*
	CheckboxMenuItem cmi;
	cmi = new CheckboxMenuItem("multiple");
	cmi.setState(Parameter.multiMode);
	add(cmi);
	cmi.addItemListener(new ItemListener(){
	    public void itemStateChanged(ItemEvent e) {
		CheckboxMenuItem c = (CheckboxMenuItem)e.getItemSelectable();
		Parameter.multiMode = c.getState();
	    }
	});

	addSeparator();

	cmi = new CheckboxMenuItem("leave dead");
	add(cmi);
	cmi.setState(Parameter.leaveDead);
	cmi.addItemListener(new ItemListener(){
	    public void itemStateChanged(ItemEvent e) {
		CheckboxMenuItem c = (CheckboxMenuItem)e.getItemSelectable();
		Parameter.leaveDead = c.getState();
	    }
	});

	addSeparator();

	cmi = new CheckboxMenuItem("auto repeat");
	add(cmi);
	cmi.setState(Parameter.autoRepeat);
	cmi.addItemListener(new ItemListener(){
	    public void itemStateChanged(ItemEvent e) {
		CheckboxMenuItem c = (CheckboxMenuItem)e.getItemSelectable();
		Parameter.autoRepeat = c.getState();
	    }
	});
	*/
    }
}

public class NumoebaGraph extends Applet implements ActionListener, ItemListener {
    static final int width = 600;
    static final int height = 600;

    GraphPanel graphPanel;
    Panel messagePanel;
    Panel controlPanel;    

    int seedValue = 0;

    TextField seed = new TextField("", 6);
    Button run = new Button("Go");
    Button suspend = new Button("Suspend");
    Button clear = new Button("Clear");
    
    CheckboxGroup cbg = new CheckboxGroup();
    Checkbox adagio = new Checkbox("slow", cbg, false);
    Checkbox moderato = new Checkbox("moderate", cbg, true);
    Checkbox presto = new Checkbox("fast", cbg, false);

    Label messageLabel = new Label();

    final Font messageFont = new Font("Courier", 0, 12);
    final FontMetrics messageFontMetrics = 
        messageLabel.getFontMetrics(messageFont);

    public static void main(String[] s) {
	NumoebaGraph g = new NumoebaGraph();
	g.init();
	g.start();
    }

    protected GraphPanel createGraphPanel(NumoebaGraph ng) {
	return new GraphPanel(this);
    }

    public void init() {
	Frame f = new Frame();

        MenuBar mb = new MenuBar();
        mb.add(new FileMenu());
        f.setMenuBar(mb);

	setLayout(new BorderLayout());

	graphPanel = createGraphPanel(this);
	graphPanel.setBackground(Color.white);
	add("Center", graphPanel);
	
	Panel lowerPanel = new Panel();
	lowerPanel.setLayout(new BorderLayout());
	lowerPanel.setBackground(Color.white);
	lowerPanel.setForeground(Color.black);
	add("South", lowerPanel);

	messagePanel = new Panel(new BorderLayout());
	messagePanel.setBackground(Color.white);
	messagePanel.setForeground(Color.black);
	messageLabel.setFont(messageFont);
	messagePanel.add(messageLabel);
	lowerPanel.add("North", messagePanel);
	
	controlPanel = new Panel();	
	controlPanel.setBackground(Color.white);
	controlPanel.setForeground(Color.black);
	lowerPanel.add("South", controlPanel);

	controlPanel.add(seed); seed.addActionListener(this);
	controlPanel.add(run); run.addActionListener(this);
	controlPanel.add(suspend); suspend.addActionListener(this);
	controlPanel.add(adagio); adagio.addItemListener(this);
	controlPanel.add(moderato); moderato.addItemListener(this);
	controlPanel.add(presto); presto.addItemListener(this);
	controlPanel.add(clear); clear.addActionListener(this);

	f.add(this, "Center");
	f.pack();
	f.setSize(width, height);
	f.setVisible(true);
    }


    public String statusString(long c, long size) {
	return "Clock: " + Long.toString(c) + "   " +
	    "#cells: " + Long.toString(size);
    }

    public void printMessage(String msg) {
	int x = messageFontMetrics.stringWidth(msg) + 10;
	int y = messageFontMetrics.getHeight();
	messagePanel.setSize(x, y);
	messageLabel.setText(msg);	
    }

    public void printStatus(long c, long size) {
	printMessage(statusString(c, size));
    }

    public void destroy() {
        remove(graphPanel);
        remove(messagePanel);
        remove(controlPanel);
    }

    public void start() {
	graphPanel.start();
    }

    public void stop() {
	graphPanel.stop();
    }

    public boolean isEmpty() {
	return graphPanel.nothingToDraw();
    }

    public void clear() {
	setRunning(false);
	graphPanel.clear();
	messageLabel.setText("");
	seed.setText("");
    }

    public int getSeedValue() {
	return seedValue;
    }

    protected void putSeed() {
	String s = seed.getText();
	int i = 0;
	try {
	    i = Integer.parseInt(s);
	}
	catch (NumberFormatException nfe) {
	    System.err.println("Bad number format.");
	    seed.setText("");
	    return;
	}
	if (!isEmpty()) {
	    if (seedValue == i) return;
	    else {
		clear();
		seed.setText(s);
	    }
	}
	seedValue = i;
	graphPanel.putSeed(i);
	messageLabel.setText(statusString(0, 1));
    }

    protected void putSeed(int i) {
	seed.setText(Integer.toString(i));
	seedValue = i;
	graphPanel.putSeed(i);
	messageLabel.setText(statusString(0, 1));
    }

    public void setRunning(boolean flag) {
	graphPanel.setRunning(flag);
    }

    public boolean isRunning() {
	return graphPanel.isRunning();
    }

    public void actionPerformed(ActionEvent e) {
	Object src = e.getSource();

	if (src == seed) {
	    putSeed();
	    return;
	}

	if (src == run) {
	    putSeed();
	    setRunning(true);
	    return;
	}

	if (src == suspend) {
	    setRunning(false);
	    return;
	}

	if (src == clear) {
	    clear();
	    return;
	}
    }

    public void itemStateChanged(ItemEvent e) {
	Object src = e.getSource();
	if (src == adagio) {
	    graphPanel.setUpdateInterval(100);
	    graphPanel.setUpdateRate(20);
	}
	else if (src == moderato) {
	    graphPanel.setUpdateInterval(100);
	    graphPanel.setUpdateRate(10);
	}
	else if (src == presto) {
	    graphPanel.setUpdateInterval(10);
	    graphPanel.setUpdateRate(5);
	}
    }
}
