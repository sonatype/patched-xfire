package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A WS-Addressing endpoint reference.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AddressingHeadersFactory200408
    extends AbstactAddressingHeadersFactory
{

    public AddressingHeaders createHeaders(Element root)
    {
        AddressingHeaders headers = new AddressingHeaders();
        
        Namespace wsa = Namespace.getNamespace(WSA_NAMESPACE_200408);
        
        Element from = root.getChild(WSA_FROM, wsa);
        if (from != null)
        {
            headers.setFrom(createEPR(from));
        }
        
        Element replyTo = root.getChild(WSA_REPLY_TO, wsa);
        if (replyTo != null)
        {
            headers.setReplyTo(createEPR(replyTo));
        }

        Element faultTo = root.getChild(WSA_FAULT_TO, wsa);
        if (faultTo != null)
        {
            headers.setFaultTo(createEPR(faultTo));
        }
        
        headers.setMessageID(getChildValue(root, WSA_MESSAGE_ID, wsa));

        Element relatesTo = root.getChild(WSA_RELATES_TO, wsa);
        if (relatesTo != null)
        {
            headers.setRelatesTo(relatesTo.getValue());
            String relation = relatesTo.getAttributeValue("RelationshipType");
            if (relation != null)
            {
                headers.setRelationshipType(stringToQName(relatesTo, relation));
            }
            else
            {
                headers.setRelationshipType(new QName(WSA_NAMESPACE_200408, "Reply"));
            }
        }
        
        headers.setTo(getChildValue(root, WSA_TO, wsa));
        String actionStr = getChildValue(root, WSA_ACTION, wsa);
        if(actionStr != null ){
            actionStr = actionStr.trim();
        }
        headers.setAction(actionStr);
        
        return headers;
    }
    
    public EndpointReference createEPR(Element eprElement)
    {
        EndpointReference epr = new EndpointReference();
        
        List anyContent = null;
        
        List elements = eprElement.getChildren();
        String version = eprElement.getNamespaceURI();
        epr.setElement(eprElement);
        for (Iterator itr = elements.iterator(); itr.hasNext();)
        {
            Element e = (Element) itr.next();
            if (e.getNamespaceURI().equals(version))
            {
                
                /*if (e.getName().equals(WSA_ADDRESS))
                {
                    // TODO : xxx
                    epr.setAddress(e);
                }
                else */if (e.getName().equals(WSA_SERVICE_NAME))
                {
                    epr.setServiceName(elementToQName(e));
                    epr.setEndpointName(e.getAttributeValue(WSA_ENDPOINT_NAME, version));
                }
                else if (e.getName().equals(WSA_INTERFACE_NAME))
                {
                    epr.setInterfaceName(elementToQName(e));
                }
                else if (e.getName().equals(WSA_POLICIES))
                {
                    List policies = new ArrayList();
                    
                    List polEls = e.getChildren();
                    for (Iterator pitr = polEls.iterator(); pitr.hasNext();)
                    {
                        policies.add(pitr.next());
                    }
                    epr.setPolicies(policies);
                }/*
                else if (e.getName().equals(WSA_REFERENCE_PROPERTIES))
                {
                    List props = new ArrayList();
                    
                    List polEls = e.getChildren();
                    for (int j = 0; j < polEls.size(); j++)
                    {
                        props.add(polEls.get(j));
                    }
                    
                    epr.setReferenceProperties(props);
                }
                else *//*if (e.getName().equals(WSA_REFERENCE_PARAMETERS))
                {
                    List params = new ArrayList();
                    
                    List polEls = e.getChildren();
                    for (int j = 0; j < polEls.size(); j++)
                    {
                        params.add(polEls.get(j));
                    }
                    
                    epr.setReferenceParameters(e);
                }
                else
                {
                    if (anyContent == null)
                        anyContent = new ArrayList();
                    
                    anyContent.add(e);
                }*/
            }
            
        }
        
        /*if (anyContent != null)
        {
            epr.setAny(anyContent);
        }*/
        
        return epr;
    }

    public boolean hasHeaders(Element root)
    {
        return root.getChild(WSA_ACTION, Namespace.getNamespace(WSA_NAMESPACE_200408)) != null;
    }

    public void writeHeaders(Element root, AddressingHeaders headers)
    {
        final Namespace ns = Namespace.getNamespace(WSA_PREFIX, WSA_NAMESPACE_200408);

        root.addNamespaceDeclaration(ns);
        
        if (headers.getTo() != null)
        {
            Element to = new Element(WSA_TO, ns);
            to.addContent(headers.getTo());
            root.addContent(to);
        }
        
        if (headers.getAction() != null)
        {
            Element action = new Element(WSA_ACTION, ns);
            action.addContent(headers.getAction());
            root.addContent(action);
        }
        
        if (headers.getFaultTo() != null)
        {
            Element faultTo = new Element(WSA_FAULT_TO, ns);
            root.addContent(faultTo);
            
            writeEPR(faultTo, headers.getFaultTo());
        }

        if (headers.getFrom() != null)
        {
            Element from = new Element(WSA_FROM, ns);
            root.addContent(from);
            
            writeEPR(from, headers.getFrom());
        }

        if (headers.getMessageID() != null)
        {
            Element messageId = new Element(WSA_MESSAGE_ID, ns);
            messageId.addContent(headers.getMessageID());
            root.addContent(messageId);
        }

        if (headers.getRelatesTo() != null)
        {
            Element relatesTo = new Element(WSA_RELATES_TO, ns);
            relatesTo.addContent(headers.getRelatesTo());
            root.addContent(relatesTo);
            
            if (headers.getRelationshipType() != null)
            {
                String value = qnameToString(root, headers.getRelationshipType());
                relatesTo.setAttribute(new Attribute(WSA_RELATIONSHIP_TYPE, value));
            }
        }
        
        if (headers.getReplyTo() != null)
        {
            Element replyTo = new Element(WSA_REPLY_TO, ns);
            root.addContent(replyTo);
            
            writeEPR(replyTo, headers.getReplyTo());
        }
    }

    public void writeEPR(Element root, EndpointReference epr)
    {
        final Namespace ns = Namespace.getNamespace(WSA_PREFIX, WSA_NAMESPACE_200408);
        
        Element address = new Element(WSA_ADDRESS, ns);
        address.addContent(epr.getAddress());
        root.addContent(address);
        
        if (epr.getServiceName() != null)
        {
            Element serviceName = new Element(WSA_SERVICE_NAME, ns);
            serviceName.addContent(qnameToString((Element) root.getParent(), epr.getServiceName()));
            root.addContent(serviceName);
            
            if (epr.getInterfaceName() != null)
            {
                String value = qnameToString((Element) root.getParent(), epr.getInterfaceName());
                serviceName.setAttribute(new Attribute("PortType", value));
            }
        }
    }

    public String getAnonymousUri()
    {
        return WSA_200408_ANONYMOUS_URI;
    }

    public String getNoneUri()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
