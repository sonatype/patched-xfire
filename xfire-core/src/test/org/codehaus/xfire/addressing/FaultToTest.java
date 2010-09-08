package org.codehaus.xfire.addressing;

import java.lang.reflect.Method;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.BadEcho;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.util.jdom.JDOMEndpoint;
import org.jdom.Document;

public class FaultToTest
    extends AbstractXFireTest
{
    Service service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        ObjectServiceFactory factory = new ObjectServiceFactory(getXFire().getTransportManager(), 
                                                                new MessageBindingProvider())
        {
            protected OperationInfo addOperation(Service endpoint, Method method, String style)
            {
                OperationInfo op = super.addOperation(endpoint, method, style);
                
                new AddressingOperationInfo("http://example.com/Echo", op);
                
                return op;
            }
        };
        factory.setStyle(SoapConstants.STYLE_MESSAGE);
        
        ((DefaultXFire)getXFire()).addInHandler(new AddressingInHandler());
        ((DefaultXFire)getXFire()).addOutHandler(new AddressingOutHandler());
        
        service = factory.create(BadEcho.class, "Echo", "urn:Echo", null);
        getServiceRegistry().register(service);
    }
    
    public void testInvoke()
        throws Exception
    {
        Transport t = getXFire().getTransportManager().getTransport(LocalTransport.BINDING_ID);
        Channel channel = t.createChannel("xfire.local://FaultReceiver");
        JDOMEndpoint endpoint = new JDOMEndpoint();
        channel.setEndpoint(endpoint);
        
        Document response = invokeService((String)null, "/org/codehaus/xfire/addressing/FaultTo.xml");
        assertNull(response);
        Thread.sleep(1000);
        assertEquals(1, endpoint.getCount());
        addNamespace("m", "urn:Echo");
        assertValid("//s:Fault", endpoint.getMessage());
    }
}
