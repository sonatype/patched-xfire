package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Element;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

public class GZIPTest
    extends AbstractXFireTest
{
    private Service service;
    private Server server;

    public void setUp() throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        service.setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);

        // Create the server
        server = new Server(8391);
        ServletHandler handler = new ServletHandler ();
        
        Context context = new Context(server,"/",Context.SESSIONS);
        
        ServletHolder servlet = new ServletHolder(new XFireServlet());
        handler.addServlet(servlet);
        
        context.addServlet(servlet, "/*");
        
        context.addFilter("com.planetj.servlet.filter.compression.CompressingFilter", "/*", 0);
        
        
        String home = System.getProperty("jetty.home",".");
        context.setResourceBase(home);
        
        context.setAttribute(XFireServlet.XFIRE_INSTANCE, getXFire());
        
        // Start the http server
        server.start ();
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

    public void testGzip() throws Exception
    {
        Echo echo = (Echo) new XFireProxyFactory().create(service, "http://localhost:8391/Echo");
        
        Client client = Client.getInstance(echo);
        client.setProperty(CommonsHttpMessageSender.GZIP_ENABLED, "true");
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Element e = echo.echo(root);
        
        assertEquals(root.getName(), e.getName());
    }
    
    public void testWithChunking() throws Exception
    {
        Echo echo = (Echo) new XFireProxyFactory().create(service, "http://localhost:8391/Echo");
        
        Client client = Client.getInstance(echo);
        client.setProperty(CommonsHttpMessageSender.GZIP_ENABLED, "true");
        client.setProperty(HttpTransport.CHUNKING_ENABLED, "true");
        
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Element e = echo.echo(root);
        
        assertEquals(root.getName(), e.getName());
    }
}
