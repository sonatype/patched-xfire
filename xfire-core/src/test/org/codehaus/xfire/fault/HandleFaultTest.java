package org.codehaus.xfire.fault;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * XFireTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class HandleFaultTest
    extends AbstractXFireTest
{
    Service service;

    public void setUp()
        throws Exception
    {
        super.setUp();

        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();

        service = osf.create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        getServiceRegistry().register(service);
    }

    public void testInFault()
        throws Exception
    {
        TestHandler handler = new TestHandler();
        handler.before(FaultThrower.class.getName());

        service.addInHandler(handler);
        service.addInHandler(new FaultThrower());

        invokeService("Echo", "/org/codehaus/xfire/echo11.xml");

        assertEquals(1, handler.invoked);
        assertEquals(1, handler.faultHandled);

    }

    public void testOutFault()
        throws Exception
    {
        TestHandler handler = new TestHandler();
        service.addInHandler(handler);
        
        TestHandler outHandler = new TestHandler();
        outHandler.before(FaultThrower.class.getName());
        service.addOutHandler(outHandler);
        service.addOutHandler(new FaultThrower());
    
        invokeService("Echo", "/org/codehaus/xfire/echo11.xml");
    
        assertEquals(1, handler.invoked);
        assertEquals(1, handler.faultHandled);
        assertEquals(1, outHandler.invoked);
        assertEquals(1, outHandler.faultHandled);

    }
    
    public static class TestHandler
        extends AbstractHandler
    {
        int faultHandled = 0;

        int invoked = 0;

        public void invoke(MessageContext context)
            throws Exception
        {
            invoked++;
        }

        public void handleFault(XFireFault fault, MessageContext context)
        {
            faultHandled++;
        }
    }

    public static class FaultThrower
        extends AbstractHandler
    {
        public void invoke(MessageContext context)
            throws Exception
        {
            throw new XFireFault("Fault!", XFireFault.SENDER);
        }
    }
}
