package org.codehaus.xfire.client;

import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.jdom.Element;

public class XFireProxyTest
        extends AbstractXFireTest
{
    private XFireProxyFactory factory;
    private Service service;
    private Transport transport;
    
    public void setUp() throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        getServiceRegistry().register(service);
        factory = new XFireProxyFactory();
        
        transport = getTransportManager().getTransport(LocalTransport.BINDING_ID);
    }
    
    public void testHandleEquals()
            throws Exception
    {
        Echo echoProxy1 = (Echo) factory.create(service, transport, "");

        assertEquals(echoProxy1, echoProxy1);
    }

    public void testHandleHashCode()
            throws Exception
    {
        Echo echoProxy = (Echo) factory.create(service, transport, "");
        
        assertTrue(echoProxy.hashCode() != 0);
    }
    
    public void testInvoke() throws Exception
    {
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        Echo echo = (Echo) factory.create(service, transport, "xfire.local://Echo");
        
        Element e = echo.echo(root);
        assertEquals(root.getName(), e.getName());
    }
    
    public void testInvokeDifferentBinding() throws Exception
    {
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Service serviceModel = new ObjectServiceFactory(new MessageBindingProvider()).create(Echo.class);
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        Echo echo = (Echo) factory.create(serviceModel, "xfire.local://Echo");
        
        Element e = echo.echo(root);
        assertEquals(root.getName(), e.getName());
    }
    
}