package org.codehaus.xfire.wsdl11.parser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.SoapFaultSerializer;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.transport.TransportManager;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Configures a prexisting Service via a WSDL.
 * 
 * @author Dan Diephouse
 */
public class WSDLServiceConfigurator
{
    protected final Definition definition;
    private List bindingAnnotators = new ArrayList();

    private TransportManager transportManager =
        XFireFactory.newInstance().getXFire().getTransportManager();
    
    private ServiceInfo serviceInfo;
    private BindingProvider bindingProvider;
    private Service service;
    private javax.wsdl.Service wservice;
    private PortType portType;
    private Collection ports;
    private boolean initService = false;
    
    
    public WSDLServiceConfigurator(Service service, URL url, TransportManager transportManager)
        throws WSDLException, IOException
    {
        this(service, 
             WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(url.openStream())), 
             transportManager);
    }
    

    public WSDLServiceConfigurator(Service service, Definition def, TransportManager transportManager)
    {
        this.service = service;
        this.serviceInfo = service.getServiceInfo();
        this.bindingProvider = service.getBindingProvider();
        this.transportManager = transportManager;
        
        this.definition = def;
        
        this.wservice = definition.getService(service.getName());

        
        if (wservice == null) 
            throw new XFireRuntimeException("Could not find service in wsdl: " + service.getName());
        
        setWrapped();
        
        ports = new ArrayList();
        for (Iterator itr = wservice.getPorts().values().iterator(); itr.hasNext();)
        {
            Port p = (Port) itr.next();
            
            if (p.getBinding().getPortType().getQName().equals(serviceInfo.getPortType()))
            {
                ports.add(p);
            }
        }
        
        bindingAnnotators.add(new SoapBindingAnnotator());
    }
    
    private void setWrapped()
    {
        XmlSchemaCollection schemas = new XmlSchemaCollection();

        if (definition.getTypes() == null)
        {
            service.getServiceInfo().setWrapped(false);
            return;
        }
        
        for (Iterator itr = definition.getTypes().getExtensibilityElements().iterator(); itr.hasNext();)
        {
            ExtensibilityElement ee = (ExtensibilityElement) itr.next();
            
            if (ee instanceof UnknownExtensibilityElement)
            {
                UnknownExtensibilityElement uee = (UnknownExtensibilityElement) ee;
                schemas.read(uee.getElement());
            }
            else
            {
                // if we are using wsdl4j >= 1.5.1, a specific extensibility
                // element is defined for schemas, so try retrieve the element
                try 
                {
                    Method mth = ee.getClass().getMethod("getElement", new Class[0]);
                    Object val = mth.invoke(ee, new Object[0]);
                    schemas.read((Element) val);
                } catch (Exception e) {
                    // Ignore exceptions ?
                }
            }
        }
        
        boolean wrapped = true;
        
        PortType portType = definition.getPortType(service.getServiceInfo().getPortType());
    
        if (portType == null)
            throw new XFireRuntimeException("Could not find port type " + service.getServiceInfo().getPortType());
        
        for (Iterator itr = portType.getOperations().iterator(); itr.hasNext();)
        {
            Operation op = (Operation) itr.next();
            if (!WSDLServiceBuilder.isWrapped(op, schemas))
            {
                wrapped = false;
                break;
            }
        }

        service.getServiceInfo().setWrapped(wrapped);
    }

    public WSDLServiceConfigurator(ServiceInfo serviceInfo,
                                   Definition definition, 
                                   javax.wsdl.Service wservice,
                                   PortType portType,
                                   Collection ports,
                                   BindingProvider bindingProvider,
                                   TransportManager transportManager)
    {
        this.definition = definition;
        this.ports = ports;
        this.wservice = wservice;
        this.portType = portType;
        this.bindingProvider = bindingProvider;
        this.transportManager = transportManager;
        this.serviceInfo = serviceInfo;
        
        initService = true;
   
        bindingAnnotators.add(new SoapBindingAnnotator());
    }

    public Definition getDefinition()
    {
        return definition;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public void setTransportManager(TransportManager transportManager)
    {
        this.transportManager = transportManager;
    }

    public void configure() throws Exception
    {
        begin(wservice, portType);
        
        for (Iterator iterator1 = ports.iterator(); iterator1.hasNext();)
        {
            Port port = (Port) iterator1.next();
            Binding binding = port.getBinding();

            visit(binding);
            
            visit(port);
        }
        
        end(wservice, portType);
    }

    protected void visit(Binding binding)
    {
        BindingAnnotator ann = getBindingAnnotator(binding);
        
        if (ann != null)
        {
            ann.setBindingProvider(bindingProvider);
            ann.setDefinition(definition);
            ann.setService(service);
            ann.setTransportManager(transportManager);
            
            ann.visit(binding);
            
            List bindingOperations = binding.getBindingOperations();
            for (int i = 0; i < bindingOperations.size(); i++)
            {
                BindingOperation bindingOperation = 
                    (BindingOperation) bindingOperations.get(i);
                String opName = bindingOperation.getOperation().getName();
                OperationInfo opInfo = serviceInfo.getOperation(opName);
                
                if (opInfo == null)
                {
                    throw new XFireRuntimeException("Could not find operation " + opName + " in the service model.");
                }
                
                ann.visit(bindingOperation, opInfo);
                
                ann.visit(bindingOperation.getBindingInput(), 
                          bindingOperation.getOperation().getInput(),
                          opInfo.getInputMessage());
                
                if (opInfo.hasOutput())
                {
                    ann.visit(bindingOperation.getBindingOutput(), 
                              bindingOperation.getOperation().getOutput(),
                              opInfo.getOutputMessage());
                }
                
                Collection bindingFaults = bindingOperation.getBindingFaults().values();
                for (Iterator iterator2 = bindingFaults.iterator(); iterator2.hasNext();)
                {
                    BindingFault bindingFault = (BindingFault) iterator2.next();
                    Fault fault = bindingOperation.getOperation().getFault(bindingFault.getName());
                    FaultInfo faultInfo = opInfo.getFault(fault.getName());
                    
                    ann.visit(bindingFault, fault, faultInfo);
                }

            }
        }
    }

    protected BindingAnnotator getBindingAnnotator(Binding binding)
    {
        for (Iterator itr = bindingAnnotators.iterator(); itr.hasNext();)
        {
            BindingAnnotator ann = (BindingAnnotator) itr.next();
            if (ann.isUnderstood(binding))
            {
                ann.setService(service);
                return ann;
            }
        }
        
        return null;
    }
    
    protected void begin(javax.wsdl.Service wservice, PortType portType)
    {
        if (initService)
        {
            service = new Service(serviceInfo);
            service.setName(wservice.getQName());
        }
    }

    protected void end(javax.wsdl.Service wservice, PortType portType)
    {
        if (initService)
        {
            service.setFaultSerializer(new SoapFaultSerializer());
            service.setBindingProvider(bindingProvider);
        }
    }
    
    protected void visit(javax.wsdl.Port port)
    {
        BindingAnnotator ann = getBindingAnnotator(port.getBinding());
        if (ann != null)
        {
            ann.visit(port);
        }
    }

    public Service getService()
    {
        return service;
    }
}
