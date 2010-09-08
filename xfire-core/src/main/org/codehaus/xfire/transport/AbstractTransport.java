package org.codehaus.xfire.transport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap11Binding;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.Soap12Binding;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.util.UID;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Dec 21, 2004
 */
public abstract class AbstractTransport
    extends AbstractHandlerSupport
    implements Transport
{
    private Map/*<String uri,Channel c>*/ channels = new HashMap();
    private static final Log log = LogFactory.getLog(AbstractTransport.class);
    
    /**
     * Disposes all the existing channels.
     */
    public void dispose()
    {
        for (Iterator itr = channels.values().iterator(); itr.hasNext();)
        {
            Channel channel = (Channel) itr.next();
            log.debug("Closing channel URI: " + channel.getUri());
        	channel.close();
            itr.remove();
        }
    }

    public Channel createChannel() throws Exception
    {
        String uri = getUriPrefix() + UID.generate();
        
        Channel c = createNewChannel(uri);
        c.open();
        
        return c;
    }

    public Channel createChannel(String uri) throws Exception
    {
        Channel c = (Channel) channels.get(uri);

        if (c == null)
        {
            c = createNewChannel(uri);

            channels.put(c.getUri(), c);

            c.open();
        }

        return c;
    }

    public void close(Channel c)
    {
    }

    protected Map getChannelMap()
    {
        return channels;
    }

    public String[] getSupportedBindings()
    {
        return new String[0];
    }


    protected abstract Channel createNewChannel(String uri);
    protected abstract String getUriPrefix();
    protected abstract String[] getKnownUriSchemes();

    public boolean isUriSupported(String uri)
    {
        String[] schemes = getKnownUriSchemes();
        for (int i = 0; i < schemes.length; i++)
        {
            if (uri.startsWith(schemes[i])) return true;
        }

        return false;
    }

    public Binding findBinding(MessageContext context, Service service)
    {
        SoapVersion soapVersion = context.getCurrentMessage().getSoapVersion();
        for (Iterator itr = service.getBindings().iterator(); itr.hasNext();)
        {
            Binding binding = (Binding) itr.next();
            if (binding.getBindingId().equals(getSupportedBindings()[0]) &&
                    ((binding instanceof Soap11Binding && soapVersion instanceof Soap11) ||
                            (binding instanceof Soap12Binding && soapVersion instanceof Soap12)))
            {
                return binding;
            }
        }
        return null;
    }
}
