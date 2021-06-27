import java.io.*;
import java.util.*;

public class Parameter {
    final static String propertyFileName = "numoeba.properties";
    static Properties properties = null;

    static int collatzCoefficient = 3;
    static int collatzConstant = 1;

    static long numbosomeBound = 1000000;

    static int spawnLeafConstant = 1;
    static int spawnLeaderConstant = -1;

    static boolean multiMode = false;
    static boolean leaveDead = true;

    static boolean autoRepeat = false;

    static {
	properties = new Properties();
    }

    static void load() {
	try {
	    FileInputStream fis = new FileInputStream(propertyFileName);
	    properties.load(fis);
	    fis.close();
	}
	catch (IOException e) {
	    System.err.println("Warning: cannot read " + propertyFileName);
	}

	try {collatzCoefficient = (int)getLongValue("collatz.coefficient");}
	catch (IllegalPropertyException e){}
	try {collatzConstant = (int)getLongValue("collatz.constant");}
	catch (IllegalPropertyException e){}
	try {numbosomeBound = getLongValue("numbosome.bound");}
	catch (IllegalPropertyException e){}
	try {spawnLeafConstant = (int)getLongValue("spawn.leaf.constant");}
	catch (IllegalPropertyException e){}
	try {spawnLeaderConstant = (int)getLongValue("spawn.leader.constant");}
	catch (IllegalPropertyException e){}

	try{ multiMode = getBooleanValue("multimode");}
	catch (IllegalPropertyException e){}
	try{ leaveDead = getBooleanValue("leavedead");}
	catch (IllegalPropertyException e){}
	try{ autoRepeat = getBooleanValue("autorepeat");}
	catch (IllegalPropertyException e){}
    }
    
    static long getLongValue(String key) throws IllegalPropertyException {
	String v = properties.getProperty(key);
	if (v == null) throw new IllegalPropertyException();
	try {
	    return Long.parseLong(v);
	}
	catch (NumberFormatException e) {
	    throw new IllegalPropertyException();
	}

    }

    static boolean getBooleanValue(String key) throws IllegalPropertyException {
	String v = properties.getProperty(key);
	if (v == null) throw new IllegalPropertyException();
	if (v.equals("true")) return true;
	if (v.equals("false")) return false;
	throw new IllegalPropertyException();
    }
}


class IllegalPropertyException extends Exception {}
