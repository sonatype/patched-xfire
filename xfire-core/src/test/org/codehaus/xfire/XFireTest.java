package org.codehaus.xfire;

import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * XFireTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        Service service = getServiceFactory().create(EchoImpl.class);
        WSDLWriter writer = new ResourceWSDL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl"));
        service.setWSDLWriter(writer);

        getServiceRegistry().register(service);
    }

    public void testSoap11()
            throws Exception
    {
        Document response = invokeService("EchoImpl", "/org/codehaus/xfire/echo11.xml");

        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", response);
    }

    public void testSoap12()
        throws Exception
    {
        Document response = invokeService("EchoImpl", "/org/codehaus/xfire/echo12.xml");
        
        addNamespace("m", "urn:Echo");
        assertValid("//soap12:Body/m:echo", response);
    }
    
    public void testWSDL()
            throws Exception
    {
        Service service = (Service) getServiceRegistry().getService("EchoImpl");

        WSDLWriter wsdl = service.getWSDLWriter();

        assertNotNull(wsdl);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        getXFire().generateWSDL("EchoImpl", out);
    }
}
