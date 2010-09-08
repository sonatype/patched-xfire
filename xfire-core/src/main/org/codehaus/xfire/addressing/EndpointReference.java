package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.filter.Filter;

/**
 * @author
 * 
 * TODO : implmeent equals and hashCode
 */
public class EndpointReference
    extends Element
    implements WSAConstants
{

    private Element element;

    private QName interfaceName;

    private QName serviceName;

    private String endpointName;

    private List policies;

    public String getAddress()
    {
        return getAddressElement().getValue();
    }

    public Element getAddressElement()
    {
        return element.getChild(WSA_ADDRESS, element.getNamespace());
    }

    public String getEndpointName()
    {
        return endpointName;
    }

    public void setEndpointName(String endpointName)
    {
        this.endpointName = endpointName;
    }

    public QName getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName(QName interfaceName)
    {
        this.interfaceName = interfaceName;
    }

    public List getPolicies()
    {
        return policies;
    }

    public void setPolicies(List policies)
    {
        this.policies = policies;
    }

    public QName getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(QName serviceName)
    {
        this.serviceName = serviceName;
    }

    public Element getReferenceParametersElement()
    {
        return element.getChild(WSA_REFERENCE_PARAMETERS, element.getNamespace());
    }

    public List getReferenceParameters()
    {
        return getReferenceParametersElement().getChildren();
    }

    public Element getReferencePropertiesElement()
    {
        return element.getChild(WSA_REFERENCE_PROPERTIES, element.getNamespace());
    }

    public List getReferenceProperties()
    {
        return getReferencePropertiesElement().getChildren();
    }

    public Element getMetadataElement()
    {
        return element.getChild(WSA_METADATA, element.getNamespace());
    }

    public List getMetadata()
    {
        return getMetadataElement().getChildren();
    }

    public Element getElement()
    {
        return element;
    }

    public void setElement(Element element)
    {
        this.element = element;
    }

    public Element addContent(Collection collection)
    {
        return element.addContent(collection);
    }

    public Element addContent(Content child)
    {
        return element.addContent(child);
    }

    public Element addContent(int index, Collection c)
    {
        return element.addContent(index, c);
    }

    public Element addContent(int index, Content child)
    {
        return element.addContent(index, child);
    }

    public Element addContent(String str)
    {
        return element.addContent(str);
    }

    public void addNamespaceDeclaration(Namespace additional)
    {
        element.addNamespaceDeclaration(additional);
    }

    public Object clone()
    {
        return element.clone();
    }

    public List cloneContent()
    {
        return element.cloneContent();
    }

    public Content detach()
    {
        return element.detach();
    }

    public List getAdditionalNamespaces()
    {
        return element.getAdditionalNamespaces();
    }

    public Attribute getAttribute(String name, Namespace ns)
    {
        return element.getAttribute(name, ns);
    }

    public Attribute getAttribute(String name)
    {
        return element.getAttribute(name);
    }

    public List getAttributes()
    {
        return element.getAttributes();
    }

    public String getAttributeValue(String name, Namespace ns, String def)
    {
        return element.getAttributeValue(name, ns, def);
    }

    public String getAttributeValue(String name, Namespace ns)
    {
        return element.getAttributeValue(name, ns);
    }

    public String getAttributeValue(String name, String def)
    {
        return element.getAttributeValue(name, def);
    }

    public String getAttributeValue(String name)
    {
        return element.getAttributeValue(name);
    }

    public Element getChild(String name, Namespace ns)
    {
        return element.getChild(name, ns);
    }

    public Element getChild(String name)
    {
        return element.getChild(name);
    }

    public List getChildren()
    {
        return element.getChildren();
    }

    public List getChildren(String name, Namespace ns)
    {
        return element.getChildren(name, ns);
    }

    public List getChildren(String name)
    {
        return element.getChildren(name);
    }

    public String getChildText(String name, Namespace ns)
    {
        return element.getChildText(name, ns);
    }

    public String getChildText(String name)
    {
        return element.getChildText(name);
    }

    public String getChildTextNormalize(String name, Namespace ns)
    {
        return element.getChildTextNormalize(name, ns);
    }

    public String getChildTextNormalize(String name)
    {
        return element.getChildTextNormalize(name);
    }

    public String getChildTextTrim(String name, Namespace ns)
    {
        return element.getChildTextTrim(name, ns);
    }

    public String getChildTextTrim(String name)
    {
        return element.getChildTextTrim(name);
    }

    public List getContent()
    {
        return element.getContent();
    }

    public List getContent(Filter filter)
    {
        return element.getContent(filter);
    }

    public Content getContent(int index)
    {
        return element.getContent(index);
    }

    public int getContentSize()
    {
        return element.getContentSize();
    }

    public Iterator getDescendants()
    {
        return element.getDescendants();
    }

    public Iterator getDescendants(Filter filter)
    {
        return element.getDescendants(filter);
    }

    public Document getDocument()
    {
        return element.getDocument();
    }

    public String getName()
    {
        return element.getName();
    }

    public Namespace getNamespace()
    {
        return element.getNamespace();
    }

    public Namespace getNamespace(String prefix)
    {
        return element.getNamespace(prefix);
    }

    public String getNamespacePrefix()
    {
        return element.getNamespacePrefix();
    }

    public String getNamespaceURI()
    {
        return element.getNamespaceURI();
    }

    public Parent getParent()
    {
        return element.getParent();
    }

    public Element getParentElement()
    {
        return element.getParentElement();
    }

    public String getQualifiedName()
    {
        return element.getQualifiedName();
    }

    public String getText()
    {
        return element.getText();
    }

    public String getTextNormalize()
    {
        return element.getTextNormalize();
    }

    public String getTextTrim()
    {
        return element.getTextTrim();
    }

    public String getValue()
    {
        return element.getValue();
    }

    public int indexOf(Content child)
    {
        return element.indexOf(child);
    }

    public boolean isAncestor(Element element)
    {
        return element.isAncestor(element);
    }

    public boolean isRootElement()
    {
        return element.isRootElement();
    }

    public boolean removeAttribute(Attribute attribute)
    {
        return element.removeAttribute(attribute);
    }

    public boolean removeAttribute(String name, Namespace ns)
    {
        return element.removeAttribute(name, ns);
    }

    public boolean removeAttribute(String name)
    {
        return element.removeAttribute(name);
    }

    public boolean removeChild(String name, Namespace ns)
    {
        return element.removeChild(name, ns);
    }

    public boolean removeChild(String name)
    {
        return element.removeChild(name);
    }

    public boolean removeChildren(String name, Namespace ns)
    {
        return element.removeChildren(name, ns);
    }

    public boolean removeChildren(String name)
    {
        return element.removeChildren(name);
    }

    public List removeContent()
    {
        return element.removeContent();
    }

    public boolean removeContent(Content child)
    {
        return element.removeContent(child);
    }

    public List removeContent(Filter filter)
    {
        return element.removeContent(filter);
    }

    public Content removeContent(int index)
    {
        return element.removeContent(index);
    }

    public void removeNamespaceDeclaration(Namespace additionalNamespace)
    {
        element.removeNamespaceDeclaration(additionalNamespace);
    }

    public Element setAttribute(Attribute attribute)
    {
        return element.setAttribute(attribute);
    }

    public Element setAttribute(String name, String value, Namespace ns)
    {
        return element.setAttribute(name, value, ns);
    }

    public Element setAttribute(String name, String value)
    {
        return element.setAttribute(name, value);
    }

    public Element setAttributes(List newAttributes)
    {
        return element.setAttributes(newAttributes);
    }

    public Element setContent(Collection newContent)
    {
        return element.setContent(newContent);
    }

    public Element setContent(Content child)
    {
        return element.setContent(child);
    }

    public Parent setContent(int index, Collection collection)
    {
        return element.setContent(index, collection);
    }

    public Element setContent(int index, Content child)
    {
        return element.setContent(index, child);
    }

    public Element setName(String name)
    {
        return element.setName(name);
    }

    public Element setNamespace(Namespace namespace)
    {
        return element.setNamespace(namespace);
    }

    public Element setText(String text)
    {
        return element.setText(text);
    }

    public List getAny()
    {
        List any = new ArrayList();
        for (Iterator iter = element.getChildren().iterator(); iter.hasNext();)
        {
            Element elem = (Element) iter.next();
            if (!element.getNamespace().equals(elem.getNamespace()))
            {
                any.add(elem);
            }
        }

        return any;
    }

}