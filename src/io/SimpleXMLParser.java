package io;

import java.io.*;
import java.util.*;

// Simple Parser Class. Works by finding XML syntax and calling Handler method for specific events
// Processes by reading in one character at a time via Reader
public class SimpleXMLParser {


	// States the Parser can be in
	public enum ParserState {
		TEXT, ENTITY, OPEN_TAG, CLOSE_TAG, START_TAG, ATTRIBUTE_LVALUE, ATTRIBUTE_EQUAL, ATTRIBUTE_RVALUE, QUOTE, IN_TAG, SINGLE_TAG, COMMENT, DOCTYPE, INITIAL, DONE;
	}

	public static void parse(SimpleDocHandler doc, Reader reader) throws Exception {
		// holds state that parser currently in/was in
		Stack state_stack = new Stack();

		// holds most current path info
		Stack pathstack = new Stack();

		// setting up markers for where we are
		int depth = 0, line = 1, col = 0, characters = 0;

		// Parser starts in Preparsing state
		ParserState state = ParserState.INITIAL;
		
		// I hate writing this so now it's a var
		int quotechar = '"';

		// Buffers for not crashing on big files
		StringBuffer sb = new StringBuffer();
		StringBuffer etag = new StringBuffer();

		// Tag values
		String tagName = null, lvalue = null, rvalue = null;

		// Attribute hashtable, (like order id)
		Hashtable attributes = null;

		// Starting to read doc
		doc.startDocument();

		// If we have reached end of line we are done
		boolean eol = false;

		// Or while the source still exists
		while ((characters = reader.read()) != -1) {

			// map whitespace/terminators (\r, \r\n, and \n) to \n
			if (characters == '\n' && eol) {
				eol = false;
				continue;
			} else if (eol) {
				eol = false;
			} else if (characters == '\n') {
				line++;
				col = 0;
			} else if (characters == '\r') {
				eol = true;
				characters = '\n';
				line++;
				col = 0;
			} else {
				col++;
			}

			// early return when we're done to save time
			if (state == ParserState.DONE) {
				doc.endDocument();
				return;

				// We are between tags collecting text.
			} else if (state == ParserState.TEXT) {
				if (characters == '<') {
					state_stack.push(state);
					state = ParserState.START_TAG;
					if (sb.length() > 0) {
						doc.text(sb.toString(), pathstack);
						sb.setLength(0);
					}
				} else if (characters == '&') {
					state_stack.push(state);
					state = ParserState.ENTITY;
					etag.setLength(0);
				} else
					sb.append((char) characters);

				// we are processing a closing tag: e.g. </foo>
			} else if (state == ParserState.CLOSE_TAG) {
				if (characters == '>') {
					state = changeState(state_stack);
					tagName = sb.toString();
					sb.setLength(0);
					depth--;
					if (depth == 0) {
						state = ParserState.DONE;
					}
					doc.endElement(tagName);
					pathstack.pop();
				} else {
					sb.append((char) characters);
				}

				// we are processing a comment. We are inside
				// the <!-- .... --> looking for the -->.
			} else if (state == ParserState.COMMENT) {
				if (characters == '>' && sb.toString().endsWith("--")) {
					sb.setLength(0);
					state = changeState(state_stack);
				} else
					sb.append((char) characters);

				// We are outside the root tag element
			} else if (state == ParserState.INITIAL) {
				if (characters == '<') {
					state = ParserState.TEXT;
					state_stack.push(state);
					state = ParserState.START_TAG;
				}

				// We are inside one of these <? ... ?>
				// or one of these <!DOCTYPE ... >
			} else if (state == ParserState.DOCTYPE) {
				if (characters == '>') {
					state = changeState(state_stack);
					if (state == ParserState.TEXT)
						state = ParserState.INITIAL;
				}

				// we have just seen a < and
				// are wondering what we are looking at
				// <foo>, </foo>, <!-- ... --->, etc.
			} else if (state == ParserState.START_TAG) {
				state = changeState(state_stack);
				if (characters == '/') {
					state_stack.push(state);
					state = ParserState.CLOSE_TAG;
				} else if (characters == '?') {
					state = ParserState.DOCTYPE;
				} else {
					state_stack.push(state);
					state = ParserState.OPEN_TAG;
					tagName = null;
					attributes = new Hashtable();
					sb.append((char) characters);
				}

				// we are processing an entity, e.g. &lt;, &#187;, etc.
			} else if (state == ParserState.ENTITY) {
				if (characters == ';') {
					state = changeState(state_stack);
					String cent = etag.toString();
					etag.setLength(0);
					if (cent.equals("lt"))
						sb.append('<');
					else if (cent.equals("gt"))
						sb.append('>');
					else if (cent.equals("amp"))
						sb.append('&');
					else if (cent.equals("quot"))
						sb.append('"');
					else if (cent.equals("apos"))
						sb.append('\'');
					else if (cent.startsWith("#"))
						sb.append((char) Integer.parseInt(cent.substring(1)));
					else
						parsing_exc("Unknown entity: &" + cent + ";", line, col);
				} else {
					etag.append((char) characters);
				}

				// looking for single tag closing >
			} else if (state == ParserState.SINGLE_TAG) {
				if (tagName == null)
					tagName = sb.toString();
				if (characters != '>')
					parsing_exc("Expected > for tag: <" + tagName + "/>", line, col);
				pathstack.push(tagName);
				doc.startElement(tagName, attributes, pathstack);
				doc.endElement(tagName);
				pathstack.pop();
				if (depth == 0) {
					doc.endDocument();
					return;
				}
				sb.setLength(0);
				attributes = new Hashtable();
				tagName = null;
				state = changeState(state_stack);

				// we are processing something
				// like this <foo ... >. It could
				// still be a <!-- ... --> or something.
			} else if (state == ParserState.OPEN_TAG) {
				if (characters == '>') {
					if (tagName == null)
						tagName = sb.toString();
					sb.setLength(0);
					depth++;
					pathstack.push(tagName);
					doc.startElement(tagName, attributes, pathstack);
					tagName = null;
					attributes = new Hashtable();
					state = changeState(state_stack);
				} else if (characters == '/') {
					state = ParserState.SINGLE_TAG;
				} else if (characters == '-' && sb.toString().equals("!-")) {
					state = ParserState.COMMENT;
				} else if (characters == 'E' && sb.toString().equals("!DOCTYP")) {
					sb.setLength(0);
					state = ParserState.DOCTYPE;
				} else if (Character.isWhitespace((char) characters)) {
					tagName = sb.toString();
					sb.setLength(0);
					state = ParserState.IN_TAG;
				} else {
					sb.append((char) characters);
				}

				// We are processing the quoted right-hand side
				// of an element's attribute.
			} else if (state == ParserState.QUOTE) {
				if (characters == quotechar) {
					rvalue = sb.toString();
					sb.setLength(0);
					attributes.put(lvalue, rvalue);
					state = ParserState.IN_TAG;
					// See section the XML spec, section 3.3.3
					// on normalization processing.
				} else if (" \r\n\u0009".indexOf(characters) >= 0) {
					sb.append(' ');
				} else if (characters == '&') {
					state_stack.push(state);
					state = ParserState.ENTITY;
					etag.setLength(0);
				} else {
					sb.append((char) characters);
				}

			} else if (state == ParserState.ATTRIBUTE_RVALUE) {
				if (characters == '"' || characters == '\'') {
					quotechar = characters;
					state = ParserState.QUOTE;
				} else if (Character.isWhitespace((char) characters)) {
					;
				} else {
					parsing_exc("Error in attribute processing", line, col);
				}

			} else if (state == ParserState.ATTRIBUTE_LVALUE) {
				if (Character.isWhitespace((char) characters)) {
					lvalue = sb.toString();
					sb.setLength(0);
					state = ParserState.ATTRIBUTE_EQUAL;
				} else if (characters == '=') {
					lvalue = sb.toString();
					sb.setLength(0);
					state = ParserState.ATTRIBUTE_RVALUE;
				} else {
					sb.append((char) characters);
				}

			} else if (state == ParserState.ATTRIBUTE_EQUAL) {
				if (characters == '=') {
					state = ParserState.ATTRIBUTE_RVALUE;
				} else if (Character.isWhitespace((char) characters)) {
					;
				} else {
					parsing_exc("Error in attribute processing.", line, col);
				}

			} else if (state == ParserState.IN_TAG) {
				if (characters == '>') {
					state = changeState(state_stack);
					pathstack.push(tagName);
					doc.startElement(tagName, attributes, pathstack);
					depth++;
					tagName = null;
					attributes = new Hashtable();
				} else if (characters == '/') {
					state = ParserState.SINGLE_TAG;
				} else if (Character.isWhitespace((char) characters)) {
					;
				} else {
					state = ParserState.ATTRIBUTE_LVALUE;
					sb.append((char) characters);
				}
			}
		}
		if (state == ParserState.DONE)
			doc.endDocument();
		else
			parsing_exc("missing end tag", line, col);
	}
	
	private static ParserState changeState(Stack state) {
		if (!state.empty()) {
			return ((ParserState) state.pop());
		} else {
			return ParserState.INITIAL;
		}
	}

	private static void parsing_exc(String error, int line, int col) throws Exception {
		throw new Exception(error + " near line " + line + ", column " + col);
	}
}
