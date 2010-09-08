package org.codehaus.xfire.wsdl11.builder;

import java.util.List;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;

/**
 * Creates a WSDLBuilder for a service.
 */
public class DefaultWSDLBuilderFactory
    implements WSDLBuilderFactory
{
    private List wsdlBuilderExtensions;
    
    public DefaultWSDLBuilderFactory()
    {
    }

    public WSDLBuilder createWSDLBuilder(Service service, TransportManager transportManager)
    {
        try
        {
            WSDLBuilder builder =  new WSDLBuilder(service, transportManager);
            builder.setWSDLBuilderExtensions(wsdlBuilderExtensions);
            return builder;
        }
        catch (XFireRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Could not create wsdl builder", e);
        }
    }

    public List getWSDLBuilderExtensions()
    {
        return wsdlBuilderExtensions;
    }

    public void setWSDLBuilderExtensions(List builderExtensions)
    {
        wsdlBuilderExtensions = builderExtensions;
    }
}
