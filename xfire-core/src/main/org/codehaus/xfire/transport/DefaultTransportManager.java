package org.codehaus.xfire.transport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.transport.dead.DeadLetterTransport;
import org.codehaus.xfire.transport.http.HttpTransport;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.codehaus.xfire.transport.local.LocalTransport;

/**
 * The default <code>TransportManager</code> implementation. 
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultTransportManager
        implements TransportManager
{
    private static final Log log = LogFactory.getLog(DefaultTransportManager.class);

    private Set transports = new LinkedHashSet();
    private Map binding2Transport = new HashMap();

    
    public DefaultTransportManager()
    {
    }
    
    public DefaultTransportManager(Set transports)
    {
        this.transports.addAll(transports);
    }

    /**
     * Initializes transports for each service.  This also registers a LocalTransport and 
     * DeadLetterTransport.
     */
    public void initialize()
    {
        register(new LocalTransport());
        register(new DeadLetterTransport());
        register(new SoapHttpTransport());
        register(new HttpTransport());
    }
    
    /**
     * Disposes and unregisters each transport.
     */
    public void dispose()
    {
        for (Iterator itr = transports.iterator(); itr.hasNext();)
        {
            Transport t = (Transport) itr.next();
            
            t.dispose();
            itr.remove();
        }
    }

    public void register(Transport transport)
    {
        transports.add(transport);
        
        String[] bindingIds = transport.getSupportedBindings();
        for (int i = 0; i < bindingIds.length; i++)
        {
            binding2Transport.put(bindingIds[i], transport);
        } 
        
        log.debug("Registered transport " + transport);
    }

    public void unregister(Transport transport)
    {
        transports.remove(transport);

        String[] bindingIds = transport.getSupportedBindings();
        for (int i = 0; i < bindingIds.length; i++)
        {
            if (binding2Transport.get(bindingIds[i]) == transport)
                binding2Transport.remove(bindingIds[i]);
        } 
    }

    public Collection getTransports()
    {
        return transports;
    }

    
    public Transport getTransportForUri(String uri)
    {
        for (Iterator itr = transports.iterator(); itr.hasNext();)
        {
            Transport t = (Transport) itr.next();
            
            if (t.isUriSupported(uri)) 
            {
                return t;
            }
        }
        
        return null;
    }
    
    public Collection getTransportsForUri(String uri)
    {
        Set uritrans = new HashSet(); 
        for (Iterator itr = transports.iterator(); itr.hasNext();)
        {
            Transport t = (Transport) itr.next();
            
            if (t.isUriSupported(uri)) 
            {
                uritrans.add(t);
            }
        }
        
        return uritrans;
    }
    
    public Transport getTransport(String id)
    {
        return (Transport) binding2Transport.get(id);
    }
}