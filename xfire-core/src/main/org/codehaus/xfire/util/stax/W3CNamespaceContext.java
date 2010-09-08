/**
 * 
 */
package org.codehaus.xfire.util.stax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class W3CNamespaceContext implements NamespaceContext
{
    private Element currentNode;
    
    public String getNamespaceURI(String prefix)
    {
        String name = prefix;
        if (name.length() == 0) name = "xmlns";
        else name = "xmlns:" + prefix;
        
        return getNamespaceURI(currentNode, name);
    }

    private String getNamespaceURI(Element e, String name)
    {
        Attr attr = e.getAttributeNode(name);
        if (attr == null)
        {
            Node n = e.getParentNode();
            if (n instanceof Element && n != e)
            {
                return getNamespaceURI((Element) n, name);
            }
        }
        else
        {
            return attr.getValue();
        }
        
        return null;
    }
    
    public String getPrefix(String uri)
    {
        return getPrefix(currentNode, uri);
    }

    private String getPrefix(Element e, String uri)
    {
        NamedNodeMap attributes = e.getAttributes();
        if (attributes != null)
        {
            for (int i = 0; i < attributes.getLength(); i++)
            {
                Attr a = (Attr) attributes.item(i);
                
                String val = a.getValue();
                if (val != null && val.equals(uri))
                {
                    String name = a.getNodeName();
                    if (name.equals("xmlns")) return "";
                    else return name.substring(6);
                }
            }
        }
        
        Node n = e.getParentNode();
        if (n instanceof Element && n != e)
        {
            return getPrefix((Element) n, uri);
        }
        
        return null;
    }

    public Iterator getPrefixes(String uri)
    {
        List prefixes = new ArrayList();
        
        String prefix = getPrefix(uri);
        if (prefix != null) prefixes.add(prefix);
        
        return prefixes.iterator();
    }

    public Element getElement()
    {
        return currentNode;
    }

    public void setElement(Element currentNode)
    {
        this.currentNode = currentNode;
    }
}