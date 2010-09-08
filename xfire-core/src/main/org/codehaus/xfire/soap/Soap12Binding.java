package org.codehaus.xfire.soap;

import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

/**
 * A SOAP 1.2 Binding.
 * @author Dan Diephouse
 */
public class Soap12Binding extends AbstractSoapBinding
{
    public Soap12Binding(QName name, String bindingId, Service serviceInfo)
    {
        super(name, bindingId, serviceInfo);
    }
    
    public SoapVersion getSoapVersion()
    {
        return Soap12.getInstance();
    }

    public boolean isSoapActionRequired(OperationInfo op)
    {
        return true;
    }

    public Binding createBinding(WSDLBuilder builder, PortType portType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Port createPort(Endpoint endpoint, WSDLBuilder builder, Binding wbinding)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Port createPort(WSDLBuilder builder, Binding wbinding)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
