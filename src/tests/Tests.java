package tests;

import java.io.FileReader;
import java.util.Hashtable;
import java.util.Stack;

import io.SimpleDocHandler;
import io.SimpleXMLParser;

// Testing some cases where the Parser should handle a specific exception
public class Tests implements SimpleDocHandler {

	public static void main(String[] args) throws Exception {
		// Should pass this first test and catch exceptions in everything else
		try {
			// Correctly parses single tags
			search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\tests\\correct-single-tag.xml");
		} catch(Exception e) {
			System.out.println("Should not print this");
		}
		
		try {
			// Throws exception on incorrect single tag syntax
			search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\tests\\incorrect-single-tag1.xml");
		} catch(Exception e) {
			String error = e.getMessage();
			System.out.println(error);
			System.out.println("Correctly caught incorrect single tag\n");
		}
		
		try {
			// Throws exception on processing attribute with no quotes
			search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\tests\\attribute-processing.xml");
		} catch(Exception e) {
			String error = e.getMessage();
			System.out.println(error);
			System.out.println("Correctly caught attribute processing error\n");
		}
		
		try {
			// Throws exception on missing tag
			search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\tests\\missing-tag.xml");
		} catch(Exception e) {
			String error = e.getMessage();
			System.out.println(error);
			System.out.println("Correctly caught missing tag/end tag error\n");
		}
		
		try {
			// Another missing tag
			search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\tests\\tag-error.xml");
		} catch(Exception e) {
			String error = e.getMessage();
			System.out.println(error);
			System.out.println("Correctly caught tag error\n");
		}
		
		try {
			// Can't process some entities like &nbsp so just throw exception
			search("C:\\Users\\Dell\\Desktop\\xmltest\\XMLParser\\src\\tests\\unknown-entity.xml");
		} catch(Exception e) {
			String error = e.getMessage();
			System.out.println(error);
			System.out.println("Correctly caught unknown entity");
		}
	}
	
	public static void search(String file) throws Exception {
		FileReader fr = new FileReader(file);
		SimpleXMLParser.parse(test, fr);

		fr.close();
	}
	
	// Instantiate static class demo
		static Tests test = new Tests();

	// Don't worry about these methods for these tests
	@Override
	public void startElement(String tag, Hashtable h, Stack pathstack) {
		//System.out.println("startElement");
	}

	@Override
	public void endElement(String tag) {
		//System.out.println("endElement");		
	}

	@Override
	public void startDocument() {
		//System.out.println("startDocument");
	}

	@Override
	public void endDocument() {
		//System.out.println("endDocument");
	}

	@Override
	public void text(String str, Stack pathstack) {
		//System.out.println("text");
	}

}
