package org.codehaus.xfire.service.event;

import java.util.EventObject;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;

/**
 * An <code>Event</code> object that provides information about the source of a service endpoint-related event.
 * <code>RegistrationEvent</code> objects are generated when an <code>ServiceEndpoint</code> is registered or
 * unregistered on a <code>ServiceEndpointRegistry</code>.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see RegistrationEventListener
 * @see Service
 * @see org.codehaus.xfire.service.ServiceRegistry
 */
public class RegistrationEvent
        extends EventObject
{
    private Service endpoint;

    /**
     * Initializes a <code>RegistrationEvent</code> object initialized with the given
     * <code>ServiceEndpointRegistry</code> and <code>ServiceEndpoint</code> object.
     *
     * @param source   the endpoint registry that is the source of the event
     * @param endpoint the endpoint that has been registered
     */
    public RegistrationEvent(ServiceRegistry source, Service endpoint)
    {
        super(source);
        this.endpoint = endpoint;
    }

    /**
     * Returns the <code>ServiceEndpoint</code> for this <code>RegistrationEvent</code>.
     *
     * @return the endpoint
     */
    public Service getEndpoint()
    {
        return endpoint;
    }
}
