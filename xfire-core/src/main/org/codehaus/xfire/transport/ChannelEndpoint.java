package org.codehaus.xfire.transport;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;

/**
 * Receives messages from a channel and acts them.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ChannelEndpoint
{
    void onReceive(MessageContext context, InMessage msg);
}
