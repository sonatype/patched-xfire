package org.codehaus.xfire;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.handler.DispatchServiceHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.DefaultTransportManager;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class DefaultXFire
        extends AbstractHandlerSupport
        implements XFire
{
    private ServiceRegistry registry;

    private TransportManager transportManager;

    private List inPhases;
    private List outPhases;

    public DefaultXFire()
    {
        registry = new DefaultServiceRegistry();
        DefaultTransportManager transportManager = new DefaultTransportManager();
        transportManager.initialize();
        
        this.transportManager = transportManager;
        
        createPhases();
        createHandlers();
    }

    public DefaultXFire(final ServiceRegistry registry,
                        final TransportManager transportManager)
    {
        this.registry = registry;
        this.transportManager = transportManager;

        createPhases();
        createHandlers();
    }

    protected void createHandlers()
    {
        addInHandler(new DispatchServiceHandler());
    }

    /**
     * Creates a default list of phases for this XFire instance.
     */
    protected void createPhases()
    {
        inPhases = new ArrayList();
        inPhases.add(new Phase(Phase.TRANSPORT, 1000));
        inPhases.add(new Phase(Phase.PARSE, 2000));
        inPhases.add(new Phase(Phase.PRE_DISPATCH, 3000));
        inPhases.add(new Phase(Phase.DISPATCH, 4000));
        inPhases.add(new Phase(Phase.POLICY, 5000));
        inPhases.add(new Phase(Phase.USER, 6000));
        inPhases.add(new Phase(Phase.PRE_INVOKE, 7000));
        inPhases.add(new Phase(Phase.SERVICE, 8000));
        Collections.sort(inPhases);
        
        outPhases = new ArrayList();
        outPhases.add(new Phase(Phase.POST_INVOKE, 1000));
        outPhases.add(new Phase(Phase.POLICY, 2000));
        outPhases.add(new Phase(Phase.USER, 3000));
        outPhases.add(new Phase(Phase.TRANSPORT, 4000));
        outPhases.add(new Phase(Phase.SEND, 5000));
        Collections.sort(outPhases);
    }

    protected Service findService(final String serviceName)
    {
        Service service = getServiceRegistry().getService(serviceName);
        
        if (service == null)
        {
            throw new XFireRuntimeException("Couldn't find service " + serviceName);
        }
        
        return service;
    }

    public void generateWSDL(final String serviceName, final OutputStream out)
    {
        try
        {
            final WSDLWriter wsdl = getWSDL(serviceName);

            wsdl.write(out);
        }
        catch (IOException e)
        {
            throw new XFireRuntimeException("Couldn't generate WSDL.", e);
        }
    }

    private WSDLWriter getWSDL(final String serviceName)
    {
        final Service service = findService(serviceName);
        return service.getWSDLWriter();
    }

    public ServiceRegistry getServiceRegistry()
    {
        return registry;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public List getInPhases()
    {
        return inPhases;
    }

    public void setInPhases(List inPhases)
    {
        this.inPhases = inPhases;
    }

    public List getOutPhases()
    {
        return outPhases;
    }

    public void setOutPhases(List outPhases)
    {
        this.outPhases = outPhases;
    }
}
