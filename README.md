# XMLParser
 Parses XML SAX style
 
 This project is split into 3 packages.
 
 demos
 - Has 3 demo files and a few xml files
 
 - FindOrders.java
 -- Demo for finding order ids with amount over 100
 -- Uses test, test1, test2, test3 xml files that are structured the same as the example xml from the assignment
 
 - LargeFileDemo.java
 -- Demo for parsing large xml files
 -- Uses large (58 MB, 580 MB) files for timing how long it takes...these files were too big to put on github
 
 - StructureDemo.java
 -- Demo for showing how path can be shown for any element in an xml file
 
 
 io
 - Has 2 files
 
 - SimpleDocHandler.java
 -- Interface for handler methods that are called when some event (start document, start element, end element, etc) happens while parsing
 
 - SimpleXMLParser.java
 -- Parser class for reading through provided xml file and parsing it.
 -- Parses entities, comments, single elements, and valid xml
 -- If invalid xml is encountered it will throw some exception
 
 
 tests
 - Has single java file and a few xml files
 
 - Tests.java
 -- Class for testing out parser with valid and invalid xml
 -- Uses files in same package for testing out specific exceptions
