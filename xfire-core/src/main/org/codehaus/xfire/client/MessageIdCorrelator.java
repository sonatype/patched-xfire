package org.codehaus.xfire.client;

import java.util.Collection;
import java.util.Iterator;

import org.codehaus.xfire.MessageContext;

/**
 * Correlates a response message by the message id.
 * 
 * @author Dan Diephouse
 */
public class MessageIdCorrelator implements Correlator
{
    public Invocation correlate(MessageContext context, Collection invocations)
    {
        if (context.getId() == null) return null;
        
        synchronized (invocations) 
        {
            for (Iterator itr = invocations.iterator(); itr.hasNext();)
            {
                Invocation call = (Invocation) itr.next();
                
                if (call.getContext() != null &&
                    call.getContext().getId() != null &&
                    call.getContext().getId().equals(context.getId()))
                {
                    return call;
                }
            }
        }
        
        return null;
    }
}