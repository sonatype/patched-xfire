package org.codehaus.xfire.transport.http;

import java.io.IOException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.soap.SoapConstants;

public abstract class AbstractMessageSender
{

    public static final String MESSAGE_SENDER_CLASS_NAME = "messageSender.className";

    private OutMessage message;
    private MessageContext context;
    
    public AbstractMessageSender(OutMessage message, MessageContext context)
    {
        this.message = message;
        this.context = context;
    }
    
    public abstract void open() throws IOException, XFireException;
    public abstract void send() throws IOException, XFireException;
    public abstract void close() throws XFireException;
    public abstract boolean hasResponse();
    
    /**
     * Returns 0 if no error returned from server. Error code in otherway.
     * @return
     */
    public abstract int getStatusCode();
    
    public abstract InMessage getInMessage() throws IOException;
    
    public MessageContext getMessageContext()
    {
        return context;
    }

    public void setMessageContext(MessageContext context)
    {
        this.context = context;
    }

    public OutMessage getMessage()
    {
        return message;
    }

    public void setMessage(OutMessage message)
    {
        this.message = message;
    }

    public String getEncoding()
    {
        return message.getEncoding();
    }

    public String getSoapAction()
    {
        return (String) message.getProperty(SoapConstants.SOAP_ACTION);
    }
    
    public String getQuotedSoapAction()
    {
        String action = getSoapAction();
        
        if (action == null)
            action = "";
        
        return "\"" + action + "\"";
    }
    
    public String getUri()
    {
        return message.getUri();
    }

}   
