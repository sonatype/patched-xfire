package org.codehaus.xfire.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.handler.AbstractHandlerSupport;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.invoker.Invoker;
import org.codehaus.xfire.wsdl.WSDLWriter;

/**
 * Represents a service endpoint. A service's sole job is to process xml messages. The
 * Binding is is the central processing point.  
 * <p>
 * The binding is then responsible for taking the SOAP Body and binding it to something - JavaBeans,
 * XMLBeans, W3C DOM tree, etc.
 * <p>
 * The <code>ServiceInfo</code> represents all the metadata that goes along with the service.
 * 
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see ServiceInfo
 * @see org.codehaus.xfire.service.binding.SOAPBinding
 */
public class Service
    extends AbstractHandlerSupport
    implements Visitable
{
    public final static String ROLE = Service.class.getName();

    public final static String DISABLE_WSDL_GENERATION="wsdl.generation.disabled";
    
    private QName name;
    
    private ServiceInfo service;
    private Map bindings = new HashMap();
    private Invoker invoker;
    private BindingProvider bindingProvider;
    private Object executor;
    private MessageSerializer faultSerializer;
    private WSDLWriter wsdlWriter;

    private Map endpoints = new HashMap();
    private Map bindingToEndpoint = new HashMap();
    private Map idToBinding = new HashMap();

    /**
     * Initializes a new, default instance of the <code>Service</code> for a specified 
     * <code>ServiceInfo</code>.
     *
     * @param service the service.
     */
    public Service(ServiceInfo service)
    {
        this.service = service;
        service.setService(this);
    }

    /**
     * For testing purposes only.
     *
     */
    public Service()
    {
    }
    
    /**
     * Accepts the given visitor. Iterates over all the contained service.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.startEndpoint(this);
        service.accept(visitor);
        visitor.endEndpoint(this);
    }

    public Object getExecutor()
    {
        return executor;
    }

    public void setExecutor(Object executor)
    {
        this.executor = executor;
    }

    public Invoker getInvoker()
    {
        return invoker;
    }

    public void setInvoker(Invoker invoker)
    {
        this.invoker = invoker;
    }

    public BindingProvider getBindingProvider()
    {
        return bindingProvider;
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public MessageSerializer getFaultSerializer()
    {
        return faultSerializer;
    }

    public void setFaultSerializer(MessageSerializer faultSerializer)
    {
        this.faultSerializer = faultSerializer;
    }

    /**
     * Returns the qualified name of the service descriptor.
     *
     * @return the qualified name.
     */
    public QName getName()
    {
        return name;
    }

    /**
     * Sets the qualified name of the service descriptor.
     *
     * @param name the new qualified name.
     */
    public void setName(QName name)
    {
        this.name = name;
    }
    
    /**
     * Returns the name of this endpoint. This method simply returns the local part of the qualified name of the
     * <code>ServiceInfo</code>.
     *
     * @return the service name.
     * @see ServiceInfo#getName()
     * @see javax.xml.namespace.QName#getLocalPart()
     */
    public String getSimpleName()
    {
        return getName().getLocalPart();
    }

    public String getTargetNamespace()
    {
        return getName().getNamespaceURI();
    }
    
    /**
     * Returns the service descriptor for this endpoint.
     *
     * @return the service descriptor.
     */
    public ServiceInfo getServiceInfo()
    {
        return service;
    }

    /**
     * Returns the <code>WSDLWriter</code> for this endpoint. If a writer has not been {@link #setWSDLWriter(WSDLWriter)
     * explicitly set}, a default implementation is used.
     *
     * @return the wsdl writer.
     */
    public WSDLWriter getWSDLWriter()
    {
        return wsdlWriter;
    }

    /**
     * Sets the <code>WSDLWriter</code> for this endpoint.
     *
     * @param wsdlWriter
     */
    public void setWSDLWriter(WSDLWriter wsdlWriter)
    {
        this.wsdlWriter = wsdlWriter;
    }

    public void addBinding(Binding binding)
    {
        bindings.put(binding.getName(), binding);

        idToBinding.put(binding.getBindingId(), binding);
    }
    
    public Binding getBinding(QName name)
    {
        return (Binding) bindings.get(name);
    }
    
    public Collection getBindings()
    {
        return Collections.unmodifiableCollection(bindings.values());
    }

    public Binding getBinding(String id)
    {
        for (Iterator itr = bindings.values().iterator(); itr.hasNext();)
        {
            Binding binding = (Binding) itr.next();
            if (binding.getBindingId().equals(id))
            {
                return binding;
            }
        }
        
        return null;
    }

    public Collection getEndpoints()
    {
        return Collections.unmodifiableCollection(endpoints.values());
    }

    public void addEndpoint(Endpoint endpoint)
    {
        endpoints.put(endpoint.getName(), endpoint);

        Set eps = (Set) bindingToEndpoint.get(endpoint.getBinding().getName());
        
        if (eps == null)
        {
            eps = new HashSet();
            bindingToEndpoint.put(endpoint.getBinding().getName(), eps);
        }
        
        eps.add(endpoint);
    }

    public Endpoint getEndpoint(QName name)
    {
        return (Endpoint) endpoints.get(name);
    }
    
    public Endpoint addEndpoint(QName name, QName bindingName, String address)
    {
        Binding binding = getBinding(bindingName);
        
        if (binding == null)
        {
            throw new IllegalStateException("Invalid binding: " + bindingName);
        }
        
        Endpoint ep = new Endpoint(name, binding, address);
        addEndpoint(ep);
        return ep;
    }
    
    public Endpoint addEndpoint(QName name, Binding binding, String address)
    {
        Endpoint endpoint = new Endpoint(name, binding, address);
        
        addEndpoint(endpoint);
        
        return endpoint;
    }

    public Collection getEndpoints(QName name2)
    {
        List eps = new ArrayList();
        for (Iterator itr = endpoints.values().iterator(); itr.hasNext();)
        {
            Endpoint ep = (Endpoint) itr.next();
            if (ep.getBinding().getName().equals(name2))
            {
                eps.add(ep);
            }
        }
        return eps;
    }
}