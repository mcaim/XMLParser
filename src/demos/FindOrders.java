package demos;

import io.SimpleDocHandler;
import io.SimpleXMLParser;

import java.util.Hashtable;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.FileReader;

// This is a demo for finding order ids over 100 

public class FindOrders implements SimpleDocHandler {

	public static void main(String[] args) throws Exception {
		System.out.println();
		System.out.println("ArrayList of each order index with an amount over 100");
		System.out.println();

		System.out.println("For test.xml. Should have order id 0 and 1");
		search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\demos\\test.xml");
		System.out.println();

		System.out.println("For test.xml1. Should have order id 0,1,2,3,...553");
		search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\demos\\test1.xml");
		System.out.println();

		System.out.println("For test.xml2. Should have order id 0 only");
		search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\demos\\test2.xml");
		System.out.println();

		System.out.println("For test.xml3. Should have order id 0, 1, 2");
		search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\demos\\test3.xml");
	}

	public static void search(String filename) throws Exception {
		FileReader filereader = new FileReader(filename);
		SimpleXMLParser.parse(findorders, filereader);
		System.out.println(order_ids_greaterthan100);
		order_ids_greaterthan100.clear();

		filereader.close();
	}

	// List stores all order ids with amounts > 100
	static ArrayList<Integer> order_ids_greaterthan100 = new ArrayList<Integer>();

	// Used to keep track of current order id when checking amount
	int orderid = 0;

	// String for path
	String path = "";

	// Instantiate static class
	static FindOrders findorders = new FindOrders();

	// Implementation of SimpleDocHandler methods
	// Generally we don't care about anything other than element and text (which
	// represents the amount value)

	public void startDocument() {
		// do nothing;
	}

	public void endDocument() {
		// do nothing
	}

	// Use this method to store the current order id
	public void startElement(String elem, Hashtable h, Stack pathstack) {
		Enumeration e = h.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();

			orderid = Integer.parseInt((String) h.get(key));
		}
	}

	public void endElement(String elem) {
		// do nothing
	}

	/*
	 * If this method fires then we are reading some sort of text/value data
	 * like the amount value if we are in the right path for the amount value we
	 * can find if it is > 100 and store the id
	 */
	public void text(String text, Stack pathstack) {
		path = "";
		for (int i = 0; i < pathstack.size(); i++) {
			path += ("/" + (String) pathstack.get(i));
		}
		if (path.equals("/root/order/amount")) {
			if (Integer.parseInt(text) > 100) {
				order_ids_greaterthan100.add(orderid);
			}
		}
	}
}
