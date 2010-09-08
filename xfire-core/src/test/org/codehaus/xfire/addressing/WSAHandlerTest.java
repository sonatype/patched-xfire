package org.codehaus.xfire.addressing;

import java.lang.reflect.Method;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class WSAHandlerTest
    extends AbstractXFireTest
{
    Service service;

    public void setUp()
        throws Exception
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

        ((DefaultXFire) getXFire()).addInHandler(new AddressingInHandler());
        service = factory.create(EchoImpl.class, "Echo", "urn:Echo", null);
        getServiceRegistry().register(service);
    }

    public void testInvoke()
        throws Exception
    {
        Document response = invokeService((String)null, "/org/codehaus/xfire/addressing/echo.xml");

        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", response);
    }
}
