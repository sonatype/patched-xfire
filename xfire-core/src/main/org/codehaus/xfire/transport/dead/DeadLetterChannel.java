package org.codehaus.xfire.transport.dead;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.transport.AbstractChannel;

/**
 * A channel which does nothing except log when a message is sent from it.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DeadLetterChannel
    extends AbstractChannel
{
    private static final Log logger = LogFactory.getLog(DeadLetterChannel.class);
    
    public DeadLetterChannel(DeadLetterTransport transport)
    {
        setTransport(transport);
    }

    public void open()
        throws Exception
    {
    }

    public void send(MessageContext context, OutMessage message)
        throws XFireException
    { 
        Object body = message.getBody();
        if (body instanceof Exception)
            logger.error("Could not deliver message to " + message.getUri(), (Exception) body);
        else
            logger.error("Could not deliver message to " + message.getUri()+ ": " + body);
    }

    public void close()
    {
    }
    
    public boolean isAsync()
    {
        return true;
    }
}
