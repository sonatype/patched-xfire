package org.codehaus.xfire.service.invoker;

import java.lang.reflect.Method;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;

/**
 * An invoker which instantiates classes automatically based on the Service's
 * scope. The default scope is SCOPE_APPLICATION, which creates once instance to
 * use for the lifetime of the invoker.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:ajoo.email@gmail.com">Ben Yu</a>
 * @since Nov 16, 2004
 */
public  class ObjectInvoker extends AbstractInvoker
   
{
    /**
     * Constant to denote the implementation class for the service.
     */
    public static final String SERVICE_IMPL_CLASS = "xfire.serviceImplClass";

    // localfactory uses a ThreadLocal to ensure thread safety.
    // using localfactory, we don't need to "new" at each request.
    private static final LocalFactory localfactory = new LocalFactory(SERVICE_IMPL_CLASS);

    private final FactoryInvoker fwd;

    private final ScopePolicy policy;

    public ObjectInvoker(ScopePolicy policy)
    {
        this.policy = policy;
        this.fwd = new FactoryInvoker(localfactory, policy);
    }

    public Object invoke(Method m, Object[] params, MessageContext context)
        throws XFireFault
    {
        final Service service = context.getService();
        localfactory.setService(service);
        return fwd.invoke(m, params, context);
    }

    public Object getServiceObject(final MessageContext context) throws XFireFault {
        
        final Service service = context.getService();
        localfactory.setService(service);    
        return fwd.getServiceObject(context);
        
    }

    /**
     * Get the scope policy used by this class.
     */
    public ScopePolicy getScope()
    {
        return policy;
    }
}
