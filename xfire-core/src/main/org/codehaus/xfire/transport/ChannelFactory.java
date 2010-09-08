package org.codehaus.xfire.transport;


/**
 * Creates channels. Transports implement this interface.
 * 
 * @see org.codehaus.xfire.transport.Transport
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ChannelFactory
{
    /**
     * Create a channel with a new unique URI.
     * 
     * @return The channel.
     * @throws Exception Occurs if there was an exception creating or opening the channel.
     */
    Channel createChannel() throws Exception;
    
    /**
     * Create a channel with a specified URI.
     * 
     * @param uri The URI which represents this Channel's endpoint.
     * @return The channel.
     * @throws Exception Occurs if there was an exception creating or opening the channel.
     */
    Channel createChannel(String uri) throws Exception;
    
    void close(Channel c);
}
