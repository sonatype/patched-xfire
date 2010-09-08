package org.codehaus.xfire.addressing;

import java.lang.reflect.Method;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.TestWSAServiceImpl;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WSAddressingNotify1_1Test
    extends AbstractXFireTest
{
    private static final String SERVICE_NAME="TestWSAServiceImpl";
    
    private AddressingInData data = null;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        data = new AddressingInData();
        Service service;
        ObjectServiceFactory factory = new ObjectServiceFactory(getXFire().getTransportManager(),
                new MessageBindingProvider())
        {

            protected OperationInfo addOperation(Service endpoint, Method method, String use)
            {
                OperationInfo op = super.addOperation(endpoint, method, use);

                new AddressingOperationInfo("http://example.org/action/notify", op);

                return op;
            }
        };
        factory.setStyle("document");
        service = factory.create(TestWSAServiceImpl.class);
        service.addInHandler(new WSATestHandler(data));
        ((DefaultXFire) getXFire()).addInHandler(new AddressingInHandler());
        ((DefaultXFire) getXFire()).addFaultHandler(new AddressingOutHandler());
        ((DefaultXFire) getXFire()).addOutHandler(new AddressingOutHandler());
        getServiceRegistry().register(service);
    }

    /**
     * @param args
     */
    public void test1100()
        throws Exception
    {
        // /soap11:Envelope/soap11:Header/wsa:Action{match}http://example.org/action/notify

        invokeService(SERVICE_NAME,
                                          "/org/codehaus/xfire/addressing/testcases/notify/soap11/message0.xml");

        assertEquals(data.getInHeaders().getAction(), "http://example.org/action/notify");

    }
    
    
}
