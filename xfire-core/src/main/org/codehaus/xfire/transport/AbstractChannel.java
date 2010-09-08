package org.codehaus.xfire.transport;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;

public abstract class AbstractChannel
    implements Channel
{
    private ChannelEndpoint receiver;
    private Transport transport;
    private String uri;

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public void setEndpoint(ChannelEndpoint receiver)
    {
        this.receiver = receiver; 
    }

    public ChannelEndpoint getEndpoint()
    {
        return receiver;
    }

    public void receive(MessageContext context, InMessage message)
    {
        if (message.getChannel() == null)
            message.setChannel(this);
        
        getEndpoint().onReceive(context, message);
    }

    public Transport getTransport()
    {
        return transport;
    }

    public void setTransport(Transport transport)
    {
        this.transport = transport;
    }

    public boolean isAsync()
    {
        return true;
    }
    
    public void close()
    {
        transport.close(this);
    }
}
