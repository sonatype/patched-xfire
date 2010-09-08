package org.codehaus.xfire.soap;

import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.handler.LocateBindingHandler;
import org.codehaus.xfire.soap.handler.FaultSoapSerializerHandler;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.soap.handler.SoapActionInHandler;
import org.codehaus.xfire.soap.handler.SoapActionOutHandler;
import org.codehaus.xfire.soap.handler.SoapBodyHandler;
import org.codehaus.xfire.soap.handler.SoapSerializerHandler;
import org.codehaus.xfire.soap.handler.ValidateHeadersHandler;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Transport;

/**
 * Provides soap messaging support to a channel by adding the SOAP handlers.
 * 
 * @see org.codehaus.xfire.soap.handler.ReadHeadersHandler
 * @see org.codehaus.xfire.soap.handler.ValidateHeadersHandler
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapTransportHelper
{
    private static final Handler readHeaders = new ReadHeadersHandler();
    private static final Handler validate = new ValidateHeadersHandler();
    private static final Handler serializer = new SoapSerializerHandler();
    private static final Handler faultSerializer = new FaultSoapSerializerHandler();
    private static final Handler bindingLocater = new LocateBindingHandler();
    private static final Handler soapAction = new SoapActionInHandler();
    private static final Handler soapActionOut = new SoapActionOutHandler();
    private static final Handler soapBinding = new SoapBodyHandler();
    
    public static Transport createSoapTransport(AbstractTransport transport)
    {
        transport.addInHandler(readHeaders);
        transport.addInHandler(validate);
        transport.addInHandler(bindingLocater);
        transport.addInHandler(soapAction);
        transport.addInHandler(soapBinding);
        
        transport.addOutHandler(soapActionOut);
        transport.addOutHandler(serializer);
        transport.addFaultHandler(faultSerializer);
        
        return transport;
    }
}