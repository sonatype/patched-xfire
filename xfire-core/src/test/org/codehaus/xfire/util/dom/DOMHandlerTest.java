package org.codehaus.xfire.util.dom;

import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class DOMHandlerTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        Service service = getServiceFactory().create(EchoImpl.class);

        service.addInHandler(new DOMInHandler());
        service.addOutHandler(new DOMOutHandler());
        
        getServiceRegistry().register(service);
    }

    public void testSoap11()
            throws Exception
    {
        Document response = invokeService("EchoImpl", "/org/codehaus/xfire/echo11.xml");

        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", response);
    }
}
