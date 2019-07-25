package io;

import java.util.*;

// Implement these methods according to what you want done when an event fires
public interface SimpleDocHandler {

	// Called when an element begins
	public void startElement(String tag, Hashtable h, Stack pathstack) throws Exception;
	
	// Called when an element ends
	public void endElement(String tag) throws Exception;
	
	// Called when the doc starts
	public void startDocument() throws Exception;

	// Called when the doc ends
	public void endDocument() throws Exception;
	
	// Called when there is some sort of text like an attribute or value
	public void text(String str, Stack pathstack) throws Exception;
}
