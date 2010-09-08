package org.codehaus.xfire.transport.http;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Channel;
import org.jdom.Element;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;


public class HttpAuthTest
    extends AbstractXFireTest
{
    private Service service;
    private Server httpServer;

    public void setUp() throws Exception
    {
        super.setUp();
        
        httpServer = new Server(8191);
        
        
        Context context = new Context(httpServer,"/",Context.SESSIONS);
        
        
        
        ServletHolder servlet = new ServletHolder(new XFireServlet());
        
        context.addServlet(servlet, "/*");
        SecurityHandler sh = new SecurityHandler();
        context.addHandler(sh);
        
        HashUserRealm userRealm = new HashUserRealm();
        userRealm.put("user", "pass");
        userRealm.addUserToRole("user", "role");

        assertNotNull(userRealm.authenticate("user", "pass", null));
        
        sh.setUserRealm(userRealm);
        
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);;
        constraint.setRoles(new String[]{"role"});
        constraint.setAuthenticate(true);
        
        
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");
        
        context.setAttribute(XFireServlet.XFIRE_INSTANCE, getXFire());
        
        httpServer.start();
        
        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        service.setBindingProvider(new MessageBindingProvider());

        getServiceRegistry().register(service);
    }

    protected XFire getXFire()
    {
        XFireFactory factory = XFireFactory.newInstance();
        return factory.getXFire();
    }

    protected void tearDown()
        throws Exception
    {
        httpServer.stop();
        
        super.tearDown();
    }

    public void testProxy() throws MalformedURLException, XFireFault
    {
        Echo echo = (Echo) new XFireProxyFactory().create(service, "http://localhost:8191/Echo");
        
        Client client = ((XFireProxy) Proxy.getInvocationHandler(echo)).getClient();
        client.setProperty(Channel.USERNAME, "user");
        client.setProperty(Channel.PASSWORD, "pass");
        
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Element e = echo.echo(root);
        
        assertEquals(root.getName(), e.getName());
    }
}
