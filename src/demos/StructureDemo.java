package demos;

import io.SimpleDocHandler;
import io.SimpleXMLParser;

import java.util.Hashtable;
import java.util.Stack;

import java.io.FileReader;

// Showing the path structure of random.xml doc
public class StructureDemo implements SimpleDocHandler {
	
	public static void main(String[] args) throws Exception {
		search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\demos\\random.xml");
	}

	public static void search(String file) throws Exception {
		FileReader filereader = new FileReader(file);
		SimpleXMLParser.parse(demo, filereader);

		filereader.close();
	}
	
	// Instantiate static class demo
	static StructureDemo demo = new StructureDemo();

	public void startDocument() {
		// nothing
	}

	public void endDocument() {
		// nothing
	}

	// just need element paths for this demo
	public void startElement(String elem, Hashtable h, Stack pathstack) {
		String path = "";
		for (int i = 0; i < pathstack.size(); i++) {
			path += ("/" + (String) pathstack.get(i));
		}
		System.out.println(path);
	}

	public void endElement(String elem) {
		// nothing
	}

	public void text(String text, Stack pathstack) {
		// nothing
	}
}
