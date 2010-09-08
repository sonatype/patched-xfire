package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

public class MustUnderstandTest
        extends AbstractXFireTest
{
    private Service endpoint;

    public void setUp()
            throws Exception
    {
        super.setUp();

        endpoint = getServiceFactory().create(EchoImpl.class);
        getServiceRegistry().register(endpoint);
    }

    public void testNotUnderstood()
            throws Exception
    {
        Document response = invokeService("EchoImpl",
                                          "/org/codehaus/xfire/handler/mustUnderstand.xml");
        assertNotNull(response);
        assertValid("//s:Fault", response);
    }

    public void testRequestUnderstand()
            throws Exception
    {
        UnderstandingHandler handler = new UnderstandingHandler();
        endpoint.addInHandler(handler);

        Document response = invokeService("EchoImpl",
                                          "/org/codehaus/xfire/handler/mustUnderstand.xml");

        assertNoFault(response);
    }

    public class UnderstandingHandler
            extends AbstractHandler
    {
        public void invoke(MessageContext context)
                throws Exception
        {
        }

        public String[] getRoles()
        {
            return super.getRoles();
        }

        public QName[] getUnderstoodHeaders()
        {
            return new QName[]{new QName("urn:test", "test")};
        }
    }
}
