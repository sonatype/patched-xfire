package org.codehaus.xfire.util.stax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;

import org.codehaus.xfire.util.NamespaceHelper;
import org.jdom.Element;
import org.jdom.Namespace;

public class JDOMNamespaceContext implements NamespaceContext
{
    private Element element;

    public String getNamespaceURI(String prefix)
    {
        Namespace namespace = element.getNamespace(prefix);
        if (namespace == null) return null;
        
        return namespace.getURI();
    }

    public String getPrefix(String uri)
    {
        return NamespaceHelper.getPrefix(element, uri);
    }

    public Iterator getPrefixes(String uri)
    {
        List prefixes = new ArrayList();
        NamespaceHelper.getPrefixes(element, uri, prefixes);
        return prefixes.iterator();
    }

    public Element getElement()
    {
        return element;
    }

    public void setElement(Element element)
    {
        this.element = element;
    }
}
