package org.codehaus.xfire.transport;

import java.util.Collection;


/**
 * Registers transports for the SOAP services.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface TransportManager
{
    String ROLE = TransportManager.class.getName();

    void register(Transport transport);

    void unregister(Transport transport);

    /**
     * Get a transport for a particular binding id.
     * @param id
     * @return
     */
    Transport getTransport(String id);

    /**
     * Find the best transport for a particular URI.
     * @param uri
     * @return
     */
    Transport getTransportForUri(String uri);
    
    Collection getTransportsForUri(String uri);

    Collection getTransports();
}
