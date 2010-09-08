package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.soap.SoapTransportHelper;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.wsdl11.WSDL11Transport;

public class SoapHttpTransport
    extends HttpTransport
    implements WSDL11Transport, SoapTransport
{
    public static final String SOAP11_HTTP_BINDING = "http://schemas.xmlsoap.org/soap/http";

    public static final String SOAP12_HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";

    public SoapHttpTransport()
    {
        super();

        SoapTransportHelper.createSoapTransport(this);
    }

    public String[] getSupportedBindings()
    {
        return new String[] { SOAP11_HTTP_BINDING, SOAP12_HTTP_BINDING };
    }

    public String getName()
    {
        return "Http";
    }

    public Binding findBinding(MessageContext context, Service service)
    {
        SoapVersion version = context.getCurrentMessage().getSoapVersion();
        
        if (version instanceof Soap11)
        {
            return service.getBinding(SOAP11_HTTP_BINDING);
        }
        else if (version instanceof Soap12)
        {
            return service.getBinding(SOAP12_HTTP_BINDING);
        }
        
        return super.findBinding(context, service);
    }   
}