package org.codehaus.xfire.addressing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.XFireFault;
import org.jdom.Attribute;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.Namespace;

public class AddressingHeadersFactory200508
    extends AbstactAddressingHeadersFactory
{

  
    public String getAnonymousUri()
    {
        return WSA_200508_ANONYMOUS_URI;
    }

    protected Namespace getNamespace()
    {
        return Namespace.getNamespace(WSA_PREFIX, WSA_NAMESPACE_200508);
    }

    public String getNoneUri()
    {
        return "http://www.w3.org/2005/08/addressing/none";
    }

    public AddressingHeaders createHeaders(Element root)
        throws XFireFault
    {
        AddressingHeaders headers = new AddressingHeaders();
    
        Namespace wsa = getNamespace();
    
        Element from = root.getChild(WSA_FROM, wsa);
        if (from != null)
        {
            headers.setFrom(createEPR(from));
        }else{
            headers.setFrom(createDefaultEPR());
        }
    
        Element replyTo = root.getChild(WSA_REPLY_TO, wsa);
        if (replyTo != null)
        {
            headers.setReplyTo(createEPR(replyTo));
        }else{
            headers.setReplyTo(createDefaultEPR());
        }
        assertSingle(root,WSA_FAULT_TO,wsa);
        Element faultTo = root.getChild(WSA_FAULT_TO, wsa); 
        if (faultTo != null)
        {
            headers.setFaultTo(createEPR(faultTo));
        }else{
            headers.setFaultTo(createDefaultEPR());
        }
    
        Element messageId = root.getChild(WSA_MESSAGE_ID, wsa);
        if (messageId != null)
        {
            headers.setMessageID(messageId.getValue());
        }
    
        Element relatesTo = root.getChild(WSA_RELATES_TO, wsa);
        if (relatesTo != null)
        {
            headers.setRelatesTo(relatesTo.getValue());
        }
        
        Element to = root.getChild(WSA_TO, wsa);
        if (to != null)
        {
            headers.setTo(to.getValue());
        }
    
        Element action = root.getChild(WSA_ACTION, wsa);
        if (action != null)
        {
            headers.setAction(action.getValue().trim());
        }
    
        return headers;
    }

    private void assertSingle(Element root, String tagName, Namespace wsa)
        throws XFireFault
    {
        List list = root.getChildren(tagName,wsa);
        if( list!= null ){
            if(list.size()> 1){
                
                XFireFault fault = new XFireFault("Invalid header",new QName(wsa.getURI(),"Sender"));
                fault.addNamespace("wsa",wsa.getURI());
                fault.setSubCode(new QName(wsa.getURI(),"InvalidAddressingHeader"));
                Element detail = new Element("ProblemHeaderQName",wsa);
                detail.addContent(tagName);
                fault.setDetail(detail);
                throw fault;
            }
        }
        
    }

    public EndpointReference createDefaultEPR()
    {
        DefaultJDOMFactory factory = new DefaultJDOMFactory();
        Element eprElem = factory.element("EPR", getNamespace());
        Element addressElem = factory.element(WSA_ADDRESS, getNamespace()); 
        addressElem.addContent(factory.text(getAnonymousUri()));
        eprElem.addContent(addressElem);
            
        
        return createEPR(eprElem);
    }

    public EndpointReference createEPR(Element eprElement)
    {
        EndpointReference epr = new EndpointReference();
    
        List elements = eprElement.getChildren();
        String version = eprElement.getNamespaceURI();
    
        epr.setElement(eprElement);
        
    
        // This will be removed.. but later :)
        for (int i = 0; i < elements.size(); i++)
        {
            Element e = (Element) elements.get(i);
            if (e.getNamespaceURI().equals(version))
            {
    
                if (e.getName().equals(WSA_SERVICE_NAME))
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
                    for (int j = 0; j < polEls.size(); j++)
                    {
                        policies.add(polEls.get(j));
                    }
                    epr.setPolicies(policies);
                }
            }
        }
    
        return epr;
    }

    public boolean hasHeaders(Element root)
    {
        return root.getChild(WSA_ACTION, getNamespace()) != null;
    }

    public void writeHeaders(Element root, AddressingHeaders headers)
    {
        final Namespace ns = getNamespace();
    
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
        if (headers.getReferenceParameters() != null)
        {
            root.addContent(headers.getReferenceParameters());
        }
    
    }

    public void writeEPR(Element root, EndpointReference epr)
    {
        final Namespace ns = getNamespace();
    
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
                serviceName.setAttribute(new Attribute(WSA_INTERFACE_NAME, value));
            }
        }
    }

  

}
