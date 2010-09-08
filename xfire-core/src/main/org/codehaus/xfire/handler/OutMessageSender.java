package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.fault.XFireFault;

/**
 * Sends messages out via the out channel on the message exchange.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class OutMessageSender
    extends AbstractHandler
{
    
    public OutMessageSender() {
        super();
        setPhase(Phase.SEND);
    }

    public void invoke(MessageContext context)
        throws XFireFault
    {
        try
        {
            context.getOutMessage().getChannel().send(context, context.getOutMessage());
        }
        catch (XFireException e)
        {
            throw XFireFault.createFault(e);
        }
    }
}
