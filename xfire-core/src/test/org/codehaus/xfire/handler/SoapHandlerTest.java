package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class SoapHandlerTest
        extends AbstractXFireTest
{
    private CheckpointHandler reqHandler;
    private CheckpointHandler resHandler;

    public void setUp()
            throws Exception
    {
        super.setUp();
        
        Service endpoint = getServiceFactory().create(EchoImpl.class);

        reqHandler = new CheckpointHandler();
        endpoint.addInHandler(reqHandler);

        resHandler = new CheckpointHandler();
        endpoint.addOutHandler(resHandler);
        endpoint.addOutHandler(new EchoHeaderHandler());

        getServiceRegistry().register(endpoint);
    }

    public void testInvoke()
            throws Exception
    {
        Document response = invokeService("EchoImpl", "/org/codehaus/xfire/echo11.xml");

        assertTrue(reqHandler.invoked);
        assertTrue(resHandler.invoked);
    }

    public void testHeaders()
            throws Exception
    {
        Document response = invokeService("EchoImpl", "/org/codehaus/xfire/handler/headerMsg.xml");

        assertTrue(reqHandler.invoked);
        assertTrue(resHandler.invoked);
        addNamespace("e", "urn:Echo");
        assertValid("/s:Envelope/s:Header/e:echo", response);
    }

    public void testPhaseOfHandler() throws Exception
    {
        AbstractHandler handler = new EchoHeaderHandler();
        assertEquals("user", handler.getPhase());
    }
    
    public class CheckpointHandler
            extends AbstractHandler
    {
        public boolean invoked = false;

        public void invoke(MessageContext context)
                throws Exception
        {
            this.invoked = true;
        }
    }
    public class EchoHeaderHandler
        extends AbstractHandler
    {
        public void invoke(MessageContext context)
            throws Exception
        {
            context.getOutMessage().setHeader(context.getInMessage().getHeader());
        }
    }
}
