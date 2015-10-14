package formality.Util.xml;

import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.OutputStream;
import java.io.InputStream;

import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.FactoryConfigurationError;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
//import org.jdom.output.Format;

import java.io.InputStream;
import java.io.OutputStream;
/**
 * simple utilities for parsing/encodeing XML data
 */
public class XmlUtils {

  public static final String XmlCharacterEncodingTypeUTF_8 = "UTF-8";
  public static final String XmlCharacterEncodingTypeISO_8859_1 = "ISO-8859-1";

  /**
   * the url for the dtd's for the system, this must be set to the same value for
   * all the CKC systems.
   */
  public static final String dtdURLKey_ = "xml.dtd.url";
  private static String dtdURL_ = null;
  /**
   * the xml escape characters
   */
  private static final String ampersandEscape_ = "&amp;";
  private static final String lessThanEscape_ = "&lt;";
  private static final String greaterThanEscape_ = "&gt;";
  private static final String quoteEscape_ = "&apos;";
  private static final String doubleQuoteEscape_ = "&quot;";
  public final static String saxParserClassName_ = "org.apache.xerces.parsers.SAXParser";
  public final static String saxSystemParserKey_ = "javax.xml.parsers.DocumentBuilderFactory";
  public final static String systemDocumentBuilderFactoryName_ = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";


  /**
   * get the XML header
   */
  public static String xmlHeader(String doctype, String system)
  {
    StringBuffer out = new StringBuffer(256);
    // encoding used to be us-ascii but IE had problems with it
    out.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
    out.append("<!DOCTYPE ").append(doctype).append(" SYSTEM \"");
    out.append(system).append(".dtd\">\n");
    return out.toString();
  }
  /**
   * escaped special xml characters in the string and return a new version
   */
  public static String escapeXMLCharacters(String source)
  {

    String s1 = escapeAmpersand(source);
    s1 = substitute(s1, "<", lessThanEscape_);
    s1 = substitute(s1, ">", greaterThanEscape_);
    s1 = substitute(s1, "'", quoteEscape_);
    s1 = substitute(s1, "\"", doubleQuoteEscape_);
    return s1;
  }

  private static String escapeAmpersand (String source)
  {
    if (source == null) return null;
    char[] src = source.toCharArray();
    StringBuffer b = new StringBuffer();
    String ent;
    for (int i=0;i<src.length;i++)
    {
      if (src[i] != '&')
        b.append(src[i]);
      else
      {
        if (source.startsWith(ampersandEscape_,i))
          ent = ampersandEscape_;
        else if (source.startsWith(lessThanEscape_,i))
          ent = lessThanEscape_;
        else if (source.startsWith(greaterThanEscape_,i))
          ent = greaterThanEscape_;
        else if (source.startsWith(quoteEscape_,i))
          ent = quoteEscape_;
        else if (source.startsWith(doubleQuoteEscape_,i))
          ent = doubleQuoteEscape_;
        else
        {
          b.append(ampersandEscape_);
          continue;
        }
        b.append(ent);
        i += ent.length()-1;
      }
    }
    return b.toString();
  }

  /**
   * replace xml escape characters to their real value
   */
  public static String replaceXMLCharacters(String source)
  {
    String s1 = substitute(source, lessThanEscape_, "<");
    s1 = substitute(s1, greaterThanEscape_, ">");
    s1 = substitute(s1, quoteEscape_, "'");
    s1 = substitute(s1, doubleQuoteEscape_, "\"");
    s1 = substitute(s1,  ampersandEscape_, "&");
    return s1;
  }

  /**
   * parse an integer value from xml parsed data
   */
  public static int parseInt(String value)
    throws SAXException
  {
    if(value == null)
    {
      throw new SAXException("cannot parse numeric value from null string");
    }
    value = value.trim();
    return Integer.valueOf(value).intValue();
  }

  /**
   * parse an floating value from xml parsed data
   */
  public static float parseFloat(String value)
    throws SAXException
  {
    if(value == null)
    {
      throw new SAXException("cannot parse numeric value from null string");
    }
    value = value.trim();
    return Float.valueOf(value).floatValue();
  }
  /**
   * parse an boolean value from xml parsed data
   */
  public static boolean parseBoolean(String value)
    throws SAXException
  {
    if(value == null)
    {
      throw new SAXException("cannot parse numeric value from null string");
    }
    value = value.trim();
    return (Boolean.valueOf(value)).booleanValue();
  }

  /*
  public static void setXMLParser (String parser) {
System.setProperty("javax.xml.parsers.SAXParserFactory",
    parser );
  }
  */

  /** Return a SAX Parser.  Note that the specific parser to use is determined by the SAXParserFactory.
   *  Assumption:  We have previously set the javax.xml.parsers.SAXParserFactory system property to refer
   *  to a SAXParserFactoryImpl.  This is done at servlet init when it calls XMLUtils.setXMLParser();
   *  See SAXParserFactory doc on how it finds an implementation class
   */
  public static SAXParser getSAXParser (boolean isValidating) throws Exception
  {
    SAXParserFactory spf = null;

    try
    {
      spf = SAXParserFactoryImpl.newInstance();
    }
    catch (FactoryConfigurationError err)
    {
      //System.out.println(err);
      throw err;
    }


    spf.setValidating(isValidating);
    return spf.newSAXParser();
  }

  /**
   * get a jdom SAXBuilder object using the approved sax parser class as defined
   * by the saxParserClassName_ public static data member of this class.  this requires
   * that the SAX parsing class jar is included on the runtime classpath and build
   * classpath.
   * @param isValidating if the parser is validating
   * @return the SAXBuilder object
   * @throws Exception
   */
  public static SAXBuilder getSAXBuilder(boolean isValidating) throws Exception
  {
    SAXBuilder parser =new SAXBuilder(saxParserClassName_, false);
    return parser;
  }

  public static String setSystemSAXParser()
  {
    return setSystemSAXParser(systemDocumentBuilderFactoryName_);
  }
  public static String setSystemSAXParser(String saxParserClassName)
  {
    String saxClassName = System.getProperty(saxSystemParserKey_);
    if(saxParserClassName != null)
    {
      System.setProperty(saxSystemParserKey_, saxParserClassName);
    }
    return saxClassName;
  }
  /**
   * get the xml string output for a jdom document
   * @param doc the document
   * @return the xml string
   */
  public static String getXmlOutput(Document doc, String charEncodingType)
  {

        return null;
//    XMLOutputter xmlOut = new XMLOutputter();
//    xmlOut.setEncoding(charEncodingType);
//    xmlOut.setOmitEncoding(false);
//    return xmlOut.outputString(doc);
  }

  /**
   * get the xml string output for a jdom document
   * @param elem the root element to output
   * @return the xml string
   */
  public static String getXmlOutput(Element elem, String charEncodingType)
  {
//    XMLOutputter xmlOut = new XMLOutputter();
//    xmlOut.setEncoding(charEncodingType);
//    xmlOut.setOmitEncoding(false);
//    return xmlOut.outputString(elem);
      return null;
  }

  /**
   * get the xml string output for a jdom document
   * @param doc the document to output
   * @throws java.io.IOException
   */
  public static void getXmlOutput(Document doc, String charEncodingType, OutputStream outstream) throws java.io.IOException
  {
//    XMLOutputter xmlOut = new XMLOutputter();
//    xmlOut.setEncoding(charEncodingType);
//    xmlOut.setOmitEncoding(false);
//    xmlOut.output(doc, outstream);
  }

  /**
   * produces an xml fragment for an element and its value.
   * @param elementName the element name
   * @param elementValue the element value
   * @param escape if true, then escape the value of the xml element
   * @return the xml fragment
   */
  public static String getElementString(String elementName,
                                        String elementValue, boolean escape)
  {
    StringBuffer tmp = new StringBuffer();
    tmp.append("<").append(elementName).append(">");
    if(elementValue != null)
    {
      if(escape)
      {
        tmp.append(XmlUtils.escapeXMLCharacters(elementValue));
      }
      else
      {
        tmp.append(elementValue);
      }
    }
    tmp.append("</").append(elementName).append(">\n");
    return tmp.toString();
  }

  /**
   * create an xml document from an input stream
   * @param input the input stream
   * @return the jdom XML document
   * @throws Exception
   */
  public static Document getXmlDocument(InputStream input) throws Exception
  {
    // create the parser and get the root element 'simpleforms'
    SAXBuilder parser = XmlUtils.getSAXBuilder(false);
    Document doc = parser.build(input);
    return doc;
  }

  /**
   * create an xml document from an input stream
   * @param input the input stream
   * @return the jdom XML document
   * @throws Exception
   */
  public static Document getXmlDocument(String input) throws Exception
  {
    // create the parser and get the root element 'simpleforms'
    SAXBuilder parser = XmlUtils.getSAXBuilder(false);
    Document doc = parser.build(input);
    return doc;
  }

    /**
    * substitute all occurences of the find arg with the replace arg in the
    * source arg.
    * @param source the source string to replace values in.
    * @param find the substring to replace
    * @param replace the replacement substring
    * @return String the source string with substitutions
    */
    public static String substitute(String source, String find, String replace)
    {
      if(source == null || find == null || replace == null)
      {
        return null;
      }

      StringBuffer buffer = new StringBuffer();
      int index = 0;
      int rc = source.indexOf(find, index);
      final int size = find.length();
      while(rc >= 0)
      {
        if (index != rc)
        {
          buffer.append(source.substring(index, rc));
        }
        buffer.append(replace);
        index = rc + size;
        rc = source.indexOf(find, index);
      }
      // concatenate the last piece
      buffer.append(source.substring(index));
      return buffer.toString();
    }

}
