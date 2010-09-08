package org.codehaus.xfire.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * WebService assertions.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XPathAssert
{
    private static XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    
    /**
     * Assert that the following XPath query selects one or more nodes.
     * 
     * @param xpath
     */
    public static List assertValid(String xpath, Object node, Map namespaces)
        throws Exception
    {
        if (node == null) throw new NullPointerException("Node cannot be null.");
        
        List nodes = createXPath(xpath, namespaces).selectNodes(node);

        if (nodes.size() == 0)
        {
            String value;
            
            if (node instanceof Document)
            {
                value = outputter.outputString((Document) node);
            }
            else if (node instanceof Element)
            {
                value = outputter.outputString((Element) node);
            }
            else
            {
                value = node.toString();
            }
            
            throw new AssertionFailedError("Failed to select any nodes for expression:.\n" + xpath + "\n"
                    + value);
        }

        return nodes;
    }

    /**
     * Assert that the following XPath query selects no nodes.
     * 
     * @param xpath
     */
    public static List assertInvalid(String xpath, Object node, Map namespaces)
        throws Exception
    {
        if (node == null) throw new NullPointerException("Node cannot be null.");
        
        List nodes = createXPath(xpath, namespaces).selectNodes(node);

        if (nodes.size() > 0)
        {
            String value;
            
            if (node instanceof Document)
            {
                value = outputter.outputString((Document) node);
            }
            else if (node instanceof Element)
            {
                value = outputter.outputString((Element) node);
            }
            else
            {
                value = node.toString();
            }
            
            throw new AssertionFailedError("Found multiple nodes for expression:\n" + xpath + "\n"
                    + value);
        }
        
        return nodes;
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the value
     * specified.
     * 
     * @param xpath
     * @param value
     * @param node
     */
    public static void assertXPathEquals(String xpath, String value, Document node, Map namespaces)
        throws Exception
    {
        //String value2 = ((Content) createXPath( xpath, namespaces ).selectSingleNode( node )).getValue().trim();
        
        String value2 = null;
		Object valueNode = createXPath(xpath, namespaces)
				.selectSingleNode(node);
		if (valueNode instanceof Content) {
			value2 = ((Content) valueNode).getValue().trim();
		} else if (valueNode instanceof Attribute) {
			value2 = ((Attribute) valueNode).getValue().trim();
		}
        
        
        Assert.assertEquals( value, value2 );
    }

    public static void assertNoFault(Document node)
        throws Exception
    {
        Map namespaces = new HashMap();
        namespaces.put("s", Soap11.getInstance().getNamespace());
        namespaces.put("s12", Soap12.getInstance().getNamespace());
        
        assertInvalid("/s:Envelope/s:Body/s:Fault", node, namespaces);
        assertInvalid("/s12:Envelope/s12:Body/s12:Fault", node, namespaces);
    }

    public static void assertFault(Content node)
        throws Exception
    {
        Map namespaces = new HashMap();
        namespaces.put("s", Soap11.getInstance().getNamespace());
        namespaces.put("s12", Soap12.getInstance().getNamespace());
        
        assertValid("/s:Envelope/s:Body/s:Fault", node, namespaces);
        assertValid("/s12:Envelope/s12:Body/s12:Fault", node, namespaces);
    }
    
    /**
     * Create the specified XPath expression with the namespaces added via
     * addNamespace().
     */
    public static XPath createXPath( String xpathString, Map namespaces ) 
        throws Exception
    {
        XPath xpath = XPath.newInstance(xpathString);
        
        for ( Iterator itr = namespaces.keySet().iterator(); itr.hasNext(); )
        {
            String ns = (String) itr.next();
            xpath.addNamespace(ns, (String) namespaces.get(ns));
        }
    
        return xpath;
    }
}
