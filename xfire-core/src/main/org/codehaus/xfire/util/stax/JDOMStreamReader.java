package org.codehaus.xfire.util.stax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.util.FastStack;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.Text;

/**
 * 
 * Facade for DOMStreamReader using JDOM implmentation.
 * 
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class JDOMStreamReader
    extends DOMStreamReader
{
    public static String toStaxType(int jdom)
    {
        switch(jdom)
        {
        case Attribute.CDATA_TYPE: return "CDATA";
        case Attribute.ID_TYPE: return "ID";
        case Attribute.IDREF_TYPE: return "IDREF";
        case Attribute.IDREFS_TYPE: return "IDREFS";
        case Attribute.ENTITY_TYPE: return "ENTITY";
        case Attribute.ENTITIES_TYPE: return "ENTITIES";
        case Attribute.ENUMERATED_TYPE: return "ENUMERATED";
        case Attribute.NMTOKEN_TYPE: return "NMTOKEN";
        case Attribute.NMTOKENS_TYPE: return "NMTOKENS";
        case Attribute.NOTATION_TYPE: return "NOTATION";
        default: return null;
        }     
    }

    private Content content;
    
    private FastStack namespaceStack = new FastStack();
    
    private List namespaces = new ArrayList();
    
    private Map prefix2decNs;
    
    private JDOMNamespaceContext namespaceContext;
    
    /**
     * @param element
     */
    public JDOMStreamReader(Element element)
    {
        super(new ElementFrame(element, null));
        
        namespaceContext = new JDOMNamespaceContext();
        setupNamespaces(element);
    }

    private void setupNamespaces(Element element)
    {
        namespaceContext.setElement(element);
        
        if (prefix2decNs != null)
        {
            namespaceStack.push(prefix2decNs);
        }
        
        prefix2decNs = new HashMap();
        namespaces.clear();
        
        for (Iterator itr = element.getAdditionalNamespaces().iterator(); itr.hasNext();)
        {
            declare((Namespace) itr.next());
        }
        
        Namespace ns = element.getNamespace();
        
        if (shouldDeclare(ns)) declare(ns);
        
        for (Iterator itr = element.getAttributes().iterator(); itr.hasNext();)
        {
            ns = ((Attribute) itr.next()).getNamespace();
            if (shouldDeclare(ns)) declare(ns);
        }
    }

    private void declare(Namespace ns)
    {
        prefix2decNs.put(ns.getPrefix(), ns);
        namespaces.add(ns);
    }

    private boolean shouldDeclare(Namespace ns)
    {
        if (ns == Namespace.XML_NAMESPACE) return false;
        
        if (ns == Namespace.NO_NAMESPACE && getDeclaredURI("") == null)
            return false;
        
        String decUri = getDeclaredURI(ns.getPrefix());
        
        return !(decUri != null && decUri.equals(ns.getURI()));
    }

    private String getDeclaredURI(String string)
    {
        for (int i = namespaceStack.size() - 1 ; i == 0; i--)
        {
            Map namespaces = (Map) namespaceStack.get(i);
            
            Namespace dec = (Namespace) namespaces.get(string);
            
            if (dec != null) return dec.getURI();
        }
        return null;
    }

    
    protected void endElement()
    {
        if (namespaceStack.size() > 0)
            prefix2decNs = (Map) namespaceStack.pop();
    }

    /**
     * @param document
     */
    public JDOMStreamReader(Document document)
    {
        this(document.getRootElement());
    }

    public Element getCurrentElement()
    {
        return (Element) getCurrentFrame().element;
    }

    protected ElementFrame getChildFrame(int currentChild)
    {
        return new ElementFrame(getCurrentElement().getContent(currentChild), getCurrentFrame());
    }

    protected int getChildCount()
    {
        return getCurrentElement().getContentSize();
    }

    protected int moveToChild(int currentChild)
    {
        this.content = getCurrentElement().getContent(currentChild);
        
        if (content instanceof Text)
            return CHARACTERS;
        else if (content instanceof Element)
        {
            setupNamespaces((Element) content);
            return START_ELEMENT;
        }
        else if (content instanceof CDATA)
                    return CHARACTERS;
        else if (content instanceof Comment)
            return CHARACTERS;
        else if (content instanceof EntityRef)
            return ENTITY_REFERENCE;
        
        throw new IllegalStateException();
    }

    public String getElementText()
        throws XMLStreamException
    {
        if (getEventType() != START_ELEMENT)
        {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text",
                    getLocation());
        }
        int eventType = next();
        StringBuffer content  = new StringBuffer();
        while (eventType != END_ELEMENT)
        {
            if (eventType == CHARACTERS || eventType == CDATA || eventType == SPACE
                    || eventType == ENTITY_REFERENCE)
            {
                content.append(getText());
            }
            else if (eventType == PROCESSING_INSTRUCTION || eventType == COMMENT)
            {
                // skipping
            }
            else if (eventType == END_DOCUMENT)
            {
                throw new XMLStreamException(
                        "unexpected end of document when reading element text content");
            }
            else if (eventType == START_ELEMENT)
            {
                throw new XMLStreamException("element text content may not contain START_ELEMENT",
                        getLocation());
            }
            else
            {
                throw new XMLStreamException("Unexpected event type " + eventType, getLocation());
            }
            eventType = next();
        }
        return content.toString();
    
    }

    public String getNamespaceURI(String prefix)
    {
        return getCurrentElement().getNamespace(prefix).getURI();
    }

    public String getAttributeValue(String ns, String local)
    {
        return getCurrentElement().getAttributeValue(local, Namespace.getNamespace(ns));
    }

    public int getAttributeCount()
    {
        return getCurrentElement().getAttributes().size();
    }

    Attribute getAttribute(int i)
    {
        return (Attribute) getCurrentElement().getAttributes().get(i);
    }
    
    public QName getAttributeName(int i)
    {
        Attribute at = getAttribute(i);
        
        return new QName(at.getNamespaceURI(), at.getName(), at.getNamespacePrefix());
    }

    public String getAttributeNamespace(int i)
    {
        return getAttribute(i).getNamespaceURI();
    }

    public String getAttributeLocalName(int i)
    {
        return getAttribute(i).getName();
    }

    public String getAttributePrefix(int i)
    {
        return getAttribute(i).getNamespacePrefix();
    }

    public String getAttributeType(int i)
    {
        return toStaxType(getAttribute(i).getAttributeType());
    }

    public String getAttributeValue(int i)
    {
        return getAttribute(i).getValue();
    }

    public boolean isAttributeSpecified(int i)
    {
        return getAttribute(i).getValue() != null;
    }

    public int getNamespaceCount()
    {
        return namespaces.size();
    }

    Namespace getNamespace(int i)
    {
        return (Namespace) namespaces.get(i);
    }
    
    public String getNamespacePrefix(int i)
    {
        return getNamespace(i).getPrefix();
    }

    public String getNamespaceURI(int i)
    {
        return getNamespace(i).getURI();
    }

    public NamespaceContext getNamespaceContext()
    {
        return namespaceContext;
    }

    public String getText()
    {
        return ((Content) content).getValue();
    }

    public char[] getTextCharacters()
    {
        return getText().toCharArray();
    }

    public int getTextStart()
    {
        return 0;
    }

    public int getTextLength()
    {
        return getText().length();
    }

    public String getEncoding()
    {
        return null;
    }

    public QName getName()
    {
        Element el = getCurrentElement();
        
        return new QName(el.getNamespaceURI(), el.getName(), el.getNamespacePrefix());
    }

    public String getLocalName()
    {
        return getCurrentElement().getName();
    }

    public String getNamespaceURI()
    {
        return getCurrentElement().getNamespaceURI();
    }

    public String getPrefix()
    {
        return getCurrentElement().getNamespacePrefix();
    }

    public String getPITarget()
    {
        throw new UnsupportedOperationException();
    }

    public String getPIData()
    {
        throw new UnsupportedOperationException();
    }
    
}
