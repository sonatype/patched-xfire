package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.MessageContext;

/**
 * Runnables sent to Executors will extend this class. This class gives
 * access to the MessageContext to provide information in scheduling 
 * decisions.
 * @author Dan Diephouse
 */
public abstract class ServiceRunner implements Runnable
{
    private MessageContext messageContext;

    public MessageContext getMessageContext()
    {
        return messageContext;
    }

    public void setMessageContext(MessageContext messageContext)
    {
        this.messageContext = messageContext;
    }
}
