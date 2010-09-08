package org.codehaus.xfire.examples.router;

// START SNIPPET: test
import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class ServiceRouterTest
        extends AbstractXFireTest
{
    Service serviceRouter;
    Service service1;
    Service service2;
    String service1Namespace = "http://xfire.codehaus.org/Echo1";
    String service2Namespace = "http://xfire.codehaus.org/Echo2";
    
    public void setUp()
            throws Exception
    {
        super.setUp();

        // This is just an endpoint which doesn't really do anything
        serviceRouter = getServiceFactory().create(ServiceRouter.class);
        
        service1 = getServiceFactory().create(EchoImpl.class, "Echo1", service1Namespace, null);
        service2 = getServiceFactory().create(EchoImpl.class, "Echo2", service2Namespace, null);

        getServiceRegistry().register(serviceRouter);
        getServiceRegistry().register(service1);
        getServiceRegistry().register(service2);
        
        ((DefaultXFire) getXFire()).addInHandler(new ServiceRouterHandler());
    }

    public void testInvoke()
            throws Exception
    {
        Document response = invokeService(serviceRouter.getSimpleName(), 
                                          "/org/codehaus/xfire/examples/router/Echo2.xml");

        addNamespace("m", "http://xfire.codehaus.org/Echo2");
        assertValid("//m:echo", response);
        
        response = invokeService(serviceRouter.getSimpleName(),
                                 "/org/codehaus/xfire/examples/router/Echo1.xml");

        addNamespace("m", "http://xfire.codehaus.org/Echo1");
        assertValid("//m:echo", response);
    }
    
    public static interface ServiceRouter {}
}
// END SNIPPET: test