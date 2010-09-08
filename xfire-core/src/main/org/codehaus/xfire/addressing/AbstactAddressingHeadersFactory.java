package org.codehaus.xfire.addressing;

import javax.xml.namespace.QName;

import org.codehaus.xfire.util.NamespaceHelper;
import org.jdom.Element;
import org.jdom.Namespace;

public abstract class AbstactAddressingHeadersFactory
    implements WSAConstants, AddressingHeadersFactory
{
    protected String getChildValue(Element element, String localName, Namespace ns)
    {
        Element child = element.getChild(localName, ns);
        if (child == null) return null;
        
        return child.getValue();
    }
    
    protected static String qnameToString(Element root, QName qname)
    {
        String prefix = NamespaceHelper.getUniquePrefix(root, qname.getNamespaceURI());
        
        return prefix + ":" + qname.getLocalPart();
    }
    
    protected static QName elementToQName(Element el)
    {
        String value = el.getValue();
        
        return stringToQName(el, value);
    }

    protected static QName stringToQName(Element el, String value)
    {
        int colon = value.indexOf(":");
        if (colon > -1)
        {
            String prefix = value.substring(0, colon);
            String local = value.substring(colon+1);
            String uri = el.getNamespace(prefix).getURI();
            return new QName(uri, local, prefix);
        }
        else
        {
            String uri = el.getNamespaceURI();
            return new QName(value, uri);
        }
    }
}
