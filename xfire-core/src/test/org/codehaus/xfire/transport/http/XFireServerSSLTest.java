package org.codehaus.xfire.transport.http;

import java.net.MalformedURLException;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Element;

public class XFireServerSSLTest
    extends AbstractXFireTest
{
    private Service service;
    private XFireHttpServer server;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        getServiceRegistry().register(service);

        server = new XFireHttpServer(getTestFile("src/etc/keystore"), "password", "password");
        server.setPort(8443);
        server.start();

    }

    protected XFire getXFire()
    {
        XFireFactory factory = XFireFactory.newInstance();
        return factory.getXFire();
    }

    protected void tearDown()
        throws Exception
    {
        server.stop();
        
        super.tearDown();
    }
    
    public void testProxy() throws MalformedURLException, XFireFault
    {
        Echo echo = (Echo) new XFireProxyFactory().create(service, "https://localhost:8443/Echo");

        Protocol protocol = new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 8443);
        Protocol.registerProtocol("https", protocol);
        
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Element e = echo.echo(root);
        
        assertEquals(root.getName(), e.getName());
    }
}
