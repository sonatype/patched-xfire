package org.codehaus.xfire.wsdl11.parser;

import javax.wsdl.BindingFault;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Output;
import javax.wsdl.Port;

import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.transport.TransportManager;

public abstract class BindingAnnotator
{
    private Service service;
    private Definition definition;
    private TransportManager transportManager;
    private BindingProvider bindingProvider;
    
    public BindingProvider getBindingProvider()
    {
        return bindingProvider;
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public void setTransportManager(TransportManager transportManager)
    {
        this.transportManager = transportManager;
    }

    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public Definition getDefinition()
    {
        return definition;
    }

    public void setDefinition(Definition definition)
    {
        this.definition = definition;
    }

    protected abstract boolean isUnderstood(javax.wsdl.Binding binding);
    
    protected abstract Binding getBinding();

    protected void visit(BindingFault bindingFault, Fault fault, FaultInfo msg)
    {
    }

    protected void visit(javax.wsdl.BindingOutput bindingOutput, Output output, MessageInfo msg)
    {
    }

    protected void visit(javax.wsdl.BindingInput bindingInput, Input input, MessageInfo msg)
    {
    }

    protected void visit(javax.wsdl.BindingOperation operation, OperationInfo opInfo)
    {
    }
    
    protected void visit(javax.wsdl.Binding wbinding)
    {
    }

    protected void visit(Port port)
    {
    }
}
