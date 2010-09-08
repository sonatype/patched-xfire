package org.codehaus.xfire.transport;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.HandlerSupport;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;

/**
 * Transport
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Transport
    extends ChannelFactory, HandlerSupport
{
    boolean isUriSupported(String uri);
    
    String[] getSupportedBindings();
    
    void dispose();

    Binding findBinding(MessageContext context, Service service);
}
