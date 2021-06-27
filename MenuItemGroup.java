import java.awt.Menu;
import java.awt.CheckboxMenuItem;
import java.awt.ItemSelectable;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

 
public class MenuItemGroup implements ItemListener {
    Menu menu;
    Set itemSet;
    ItemListener listener;

    public MenuItemGroup(Menu m) {
	menu = m;
	itemSet = new HashSet();
    } 

    public void add(String s) {
	CheckboxMenuItem cmi = new CheckboxMenuItem(s);
	itemSet.add(cmi);
	menu.add(cmi);
	cmi.addItemListener(this);
    }

    public void add(String s, boolean state) {
	CheckboxMenuItem cmi = new CheckboxMenuItem(s);
	itemSet.add(cmi);
	menu.add(cmi);
	cmi.addItemListener(this);
	cmi.setState(state);
    }

    public void addItemListener(ItemListener l) {
	listener = l; // boo! only one listener can be registered.
    }

    public void itemStateChanged(ItemEvent e) {
	ItemSelectable item = e.getItemSelectable();
	for (Iterator i = itemSet.iterator(); i.hasNext();) {
	    CheckboxMenuItem cmi = (CheckboxMenuItem)i.next();
	    if (item.equals(cmi)) cmi.setState(true);
	    else cmi.setState(false);
	}
	listener.itemStateChanged(e);
    }
}
