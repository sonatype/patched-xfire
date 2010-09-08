package org.codehaus.xfire.addressing;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * WS-Addressing Headers from a SOAP message.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AddressingHeaders
{
    private String messageID;

    private String relatesTo;
    
    private QName relationshipType;
    
    private String action;

    private String to;

    private EndpointReference from;

    private EndpointReference replyTo;

    private EndpointReference faultTo;
    
    private List referenceParameters;
    
    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public EndpointReference getFaultTo()
    {
        return faultTo;
    }

    public void setFaultTo(EndpointReference faultTo)
    {
        this.faultTo = faultTo;
    }

    public EndpointReference getFrom()
    {
        return from;
    }

    public void setFrom(EndpointReference from)
    {
        this.from = from;
    }

    public String getMessageID()
    {
        return messageID;
    }

    public void setMessageID(String messageID)
    {
        this.messageID = messageID;
    }

    public EndpointReference getReplyTo()
    {
        return replyTo;
    }

    public void setReplyTo(EndpointReference replyTo)
    {
        this.replyTo = replyTo;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getRelatesTo()
    {
        return relatesTo;
    }

    public void setRelatesTo(String relatesTo)
    {
        this.relatesTo = relatesTo;
    }

    public QName getRelationshipType()
    {
        return relationshipType;
    }

    public void setRelationshipType(QName relationshipType)
    {
        this.relationshipType = relationshipType;
    }

    public List getReferenceParameters()
    {
        return referenceParameters;
    }

    public void setReferenceParameters(List referenceParameter)
    {
        this.referenceParameters = referenceParameter;
    }
    
}
