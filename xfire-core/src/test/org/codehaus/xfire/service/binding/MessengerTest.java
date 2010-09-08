package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class MessengerTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();
    }

    public void testInvoke()
            throws Exception
    {
        Service service = getServiceFactory().create(Messenger.class);
        getServiceRegistry().register(service);
        
        assertNotNull(service.getBindingProvider());
        
        OperationInfo info = service.getServiceInfo().getOperation("receive");
        assertEquals(1, info.getInputMessage().getMessageParts().size());
        
        invokeService("Messenger", "/org/codehaus/xfire/echo11.xml");
    }
    
    public void testExecutor()
        throws Exception
    {
        Service service = getServiceFactory().create(Messenger.class);
        service.setExecutor(new Executor());
        getServiceRegistry().register(service);
        
        invokeService("Messenger", "/org/codehaus/xfire/echo11.xml");
        
        assertTrue(Executor.run);
    }
    
    public static class Executor 
    {
        static boolean run;
        
        public void execute(Runnable r)
        {
            run = true;
            r.run();
        }
    }
}