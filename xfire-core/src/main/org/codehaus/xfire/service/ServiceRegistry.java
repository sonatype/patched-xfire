package org.codehaus.xfire.service;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.event.RegistrationEventListener;

/**
 * Defines the interface that for places to register, unregister, and get information about services.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public interface ServiceRegistry
{
    /**
     * Constant used to define the role of service registries.
     */
    public static final String ROLE = ServiceRegistry.class.getName();

    /**
     * Returns the <code>ServiceEndpoint</code> with the given name, if found. Returns <code>null</code> if not found.
     *
     * @param name the service name.
     * @return the service endpoint, or <code>null</code> if not found.
     */
    Service getService(String name);

    /**
     * Returns the <code>ServiceEndpoint</code> with the given name, if found. Returns <code>null</code> if not found.
     *
     * @param name the service name.
     * @return the service endpoint, or <code>null</code> if not found.
     */
    Service getService(QName name);

    /**
     * Registers a given <code>ServiceEndpoint</code> with this registry.
     *
     * @param endpoint the endpoint.
     */
    void register(Service endpoint);

    /**
     * Unregisters the service endpoint with the given name, if found.
     *
     * @param name the service name.
     */
    void unregister(Service service);

    /**
     * Indicates whether this registry has a service endpoint with the given name.
     *
     * @param name the service name.
     * @return <code>true</code> if this registry has a service with the given name; <code>false</code> otherwise.
     */
    boolean hasService(String name);

    /**
     * Indicates whether this registry has a service endpoint with the given name.
     *
     * @param name the service name.
     * @return <code>true</code> if this registry has a service with the given name; <code>false</code> otherwise.
     */
    boolean hasService(QName name);

    /**
     * Returns all <code>ServiceEndpoint</code> registered to this registry.
     *
     * @return all service endpoints.
     */
    Collection getServices();

    /**
     * Add a listener for registration events.
     *
     * @param listener the listener.
     */
    void addRegistrationEventListener(RegistrationEventListener listener);

    /**
     * Remove a listener for registration events.
     *
     * @param listener the listener.
     */
    void removeRegistrationEventListener(RegistrationEventListener listener);
}
