package demos;

import io.SimpleDocHandler;
import io.SimpleXMLParser;

import java.util.Hashtable;
import java.util.Stack;

import java.io.FileReader;

// Showing the path structure of random.xml doc
public class LargeFileDemo implements SimpleDocHandler {
	
	public static void main(String[] args) throws Exception {
		// bigfile is ~ 58 MB
		long startTime = System.nanoTime();
		search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\demos\\bigfile.xml");
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		System.out.println("Parsing bigfile took: "+ duration/1000000 + " ms\n");
		
		// tenmillionfile is ~ 580 MB
		startTime = System.nanoTime();
		search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\demos\\tenmillionfile.xml");
		endTime = System.nanoTime();

		duration = (endTime - startTime);
		System.out.println("Parsing tenmillionfile took: "+ duration/1000000 + " ms");
	}

	public static void search(String file) throws Exception {
		FileReader filereader = new FileReader(file);
		SimpleXMLParser.parse(timedemo, filereader);

		filereader.close();
	}
	
	// Instantiate static class demo
	static LargeFileDemo timedemo = new LargeFileDemo();

	public void startDocument() {
		// nothing
	}

	public void endDocument() {
		// nothing
	}

	// just need element paths for this demo
	public void startElement(String elem, Hashtable h, Stack pathstack) {
		// nothing
	}

	public void endElement(String elem) {
		// nothing
	}

	public void text(String text, Stack pathstack) {
		// nothing
	}
}
