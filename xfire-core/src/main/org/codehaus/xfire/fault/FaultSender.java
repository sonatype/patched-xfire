package org.codehaus.xfire.fault;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.transport.Channel;

/**
 * Sends messages out via the out channel on the message exchange.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class FaultSender
    extends AbstractHandler
{
    private static final Log logger = LogFactory.getLog(FaultSender.class);

    public FaultSender() 
    {
        super();
        setPhase(Phase.SEND);
    }

    public void invoke(MessageContext context)
        throws XFireFault
    {
        Channel faultChannel = context.getExchange().getFaultMessage().getChannel();

        try
        {
            // TODO: Check if this is a DeadLetterChannel. If its not we need to
            // try resending this message through the DeadLetterChannel.
            faultChannel.send(context, (OutMessage) context.getExchange().getFaultMessage());
        }
        catch (XFireException e)
        {
            logger.error("Could not send fault.", e);
        }
    }
}
