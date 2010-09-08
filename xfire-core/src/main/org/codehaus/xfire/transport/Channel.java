package org.codehaus.xfire.transport;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;

/**
 * A channel for communication. This can be a channel on an underlying transport -
 * like HTTP - or wrap another channel and provide additional functions - like
 * reliable messaging.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Channel
{
    /** 
     * The URI which represents that a message should be sent over a back channel, i.e.
     * an HttpServletResponse, instead of opening a new connection.
     */
    String BACKCHANNEL_URI = "urn:xfire:channel:backchannel";
    
    String USERNAME = "username";
    String PASSWORD = "password";
    String OUTPUTSTREAM = "channel.outputstream";
    
    void open() throws Exception;
    
    /**
     * Sends a message.
     * @param context
     * @param message
     * @throws XFireException Occurs if there was an error an error sending the message.
     */
    void send(MessageContext context, OutMessage message) 
        throws XFireException;
    
    void receive(MessageContext context, InMessage message);

    void setEndpoint(ChannelEndpoint receiver);

    ChannelEndpoint getEndpoint();
    
    void close();
    
    Transport getTransport();

    /**
     * @return The URI which represents this Channel's endpoint.
     */
    String getUri();
    
    boolean isAsync();
}
