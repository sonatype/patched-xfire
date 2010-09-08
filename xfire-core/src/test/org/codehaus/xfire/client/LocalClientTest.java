package org.codehaus.xfire.client;

import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.BadEcho;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.jdom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class LocalClientTest
        extends AbstractXFireTest
{
    private Service service;
    private Transport transport;
    
    public void setUp() throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(Echo.class);

        getServiceRegistry().register(service);
        transport = getXFire().getTransportManager().getTransport(LocalTransport.BINDING_ID);
    }

    public void testInvoke()
            throws Exception
    {
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");

        Client client = new Client(transport, service, "xfire.local://Echo", "xfire.local://Client");
        client.setXFire(getXFire());
        
        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];
        assertEquals(root.getName(), e.getName());
    }

    public void testFault()
            throws Exception
    {
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, BadEcho.class);

        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");

        Client client = new Client(transport, service, "xfire.local://Echo");
        client.setXFire(getXFire());
        
        OperationInfo op = service.getServiceInfo().getOperation("echo");
        try
        {
            Object[] response = client.invoke(op, new Object[] {root});

            fail("Fault was not thrown!");
        }
        catch (XFireFault fault)
        {
        }
    }
}
