package org.codehaus.xfire.transport;

import java.util.List;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;

/**
 * Wraps another channel so it is easy to provide custom functionality to any transport - such
 * as reliable messaging.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DelegatedTransport
    implements Transport
{
    private Transport transport;
    
    public DelegatedTransport(Transport transport)
    {
        this.transport = transport;
    }
    
    /**
     * The transport which this transport wraps.
     * @return
     */
    public Transport getTransport()
    {
        return transport;
    }
    
    public void dispose()
    {
        transport.dispose();
    }

    public Channel createChannel()
        throws Exception
    {
        return transport.createChannel();
    }

    public Channel createChannel(String uri)
        throws Exception
    {
        return transport.createChannel(uri);
    }

    public List getInHandlers()
    {
        return transport.getInHandlers();
    }

    public List getOutHandlers()
    {
        return transport.getOutHandlers();
    }

    public List getFaultHandlers()
    {
        return transport.getFaultHandlers();
    }

    public boolean isUriSupported(String uri)
    {
        return transport.isUriSupported(uri);
    }

    public String[] getSupportedBindings()
    {
        return transport.getSupportedBindings();
    }

    public Binding findBinding(MessageContext context, Service service)
    {
        return transport.findBinding(context, service);
    }

    public void close(Channel c)
    {
        transport.close(c);
    }
}
